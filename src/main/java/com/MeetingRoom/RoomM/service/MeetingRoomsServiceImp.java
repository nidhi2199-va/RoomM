package com.MeetingRoom.RoomM.service;

import com.MeetingRoom.RoomM.Enums.BookingStatus;
import com.MeetingRoom.RoomM.Exceptions.AccessDeniedException;
import com.MeetingRoom.RoomM.Exceptions.ResourceNotFoundException;
import com.MeetingRoom.RoomM.Exceptions.RoomAlreadyExistsException;
import com.MeetingRoom.RoomM.dao.BookingsDao;
import com.MeetingRoom.RoomM.dao.MeetingRoomsDao;
import com.MeetingRoom.RoomM.dto.*;
import com.MeetingRoom.RoomM.model.Bookings;
import com.MeetingRoom.RoomM.model.MeetingRooms;
import com.MeetingRoom.RoomM.Utils.JwtUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MeetingRoomsServiceImp implements MeetingRoomsService {

    private final MeetingRoomsDao meetingRoomsDao;
    private final BookingsDao bookingsDao;
    private final JwtUtil jwtUtil;


    public MeetingRoomsServiceImp(MeetingRoomsDao meetingRoomsDao, BookingsDao bookingsDao, JwtUtil jwtUtil) {
        this.meetingRoomsDao = meetingRoomsDao;
        this.bookingsDao = bookingsDao;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public MeetingRooms addMeetingRoom(AddRoomRequestDTO addRoomRequestDTO, String token) {
        String role = jwtUtil.extractRole(token);

        if (!"ADMIN".equals(role)) {
            throw new AccessDeniedException("Access Denied! Only Admins can add meeting rooms.");
        }

        MeetingRooms existingRoom = meetingRoomsDao.findByName(addRoomRequestDTO.getName());
        if (existingRoom != null) {
            throw new RoomAlreadyExistsException("Room name already exists.");
        }

        MeetingRooms meetingRoom = new MeetingRooms();
        meetingRoom.setName(addRoomRequestDTO.getName());
        meetingRoom.setCapacity(addRoomRequestDTO.getCapacity());

        return meetingRoomsDao.save(meetingRoom);
    }

    @Override
    public MeetingRooms updateMeetingRoom(Long roomId, UpdateRoomRequestDTO updateRoomRequestDTO, String token) {
        String role = jwtUtil.extractRole(token);

        if (!"ADMIN".equals(role)) {
            throw new AccessDeniedException("Access Denied! Only Admins can update meeting rooms.");
        }

        MeetingRooms existingRoom = meetingRoomsDao.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Meeting room not found"));

        if (!existingRoom.getName().equals(updateRoomRequestDTO.getName())) {
            MeetingRooms roomWithSameName = meetingRoomsDao.findByName(updateRoomRequestDTO.getName());
            if (roomWithSameName != null) {
                throw new RoomAlreadyExistsException("Room name already exists.");
            }
        }

        existingRoom.setName(updateRoomRequestDTO.getName());
        existingRoom.setCapacity(updateRoomRequestDTO.getCapacity());

        return meetingRoomsDao.save(existingRoom);
    }

    @Override
    public List<MeetingRoomDTO> getAllMeetingRooms(String token) {
        String role = jwtUtil.extractRole(token);

        if (!"ADMIN".equals(role)) {
            throw new AccessDeniedException("Access Denied! Only Admins can view meeting rooms.");
        }

        List<MeetingRooms> meetingRooms = meetingRoomsDao.findAll();
        return meetingRooms.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private MeetingRoomDTO mapToDTO(MeetingRooms meetingRoom) {
        return new MeetingRoomDTO(meetingRoom.getName(), meetingRoom.getCapacity());
    }

    @Override
    public RoomWithTimeSlotsDTO getRoomWithTimeSlots(Long roomId) {
        MeetingRooms room = meetingRoomsDao.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        List<TimeSlotDTO> timeSlots = bookingsDao.findByMeetingRoomId(roomId).stream()
                .map(booking -> new TimeSlotDTO(booking.getStartTime(), booking.getEndTime()))
                .collect(Collectors.toList());

        return new RoomWithTimeSlotsDTO(room.getId(), room.getName(), room.getCapacity(), timeSlots);
    }

    @Override
    public RoomAvailabilityDTO getAvailableRoomsForTimeslot(LocalDateTime requestedStart, LocalDateTime requestedEnd) {
        List<Long> availableRoomIds = meetingRoomsDao.findAllActiveRooms().stream()
                .map(MeetingRooms::getId)
                .filter(id -> isRoomAvailable(id, requestedStart, requestedEnd))
                .collect(Collectors.toList());

        return new RoomAvailabilityDTO(availableRoomIds);
    }

    @Override
    public boolean isRoomAvailable(Long roomId, LocalDateTime requestedStart, LocalDateTime requestedEnd) {
        List<Bookings> activeBookings = bookingsDao.findByMeetingRoomId(roomId)
                .stream()
                .filter(booking -> booking.getStatus() == BookingStatus.BOOKED)
                .toList();

        return activeBookings.stream()
                .noneMatch(booking -> isOverlapping(booking.getStartTime(), booking.getEndTime(), requestedStart, requestedEnd));
    }

    private boolean isOverlapping(LocalDateTime existingStart, LocalDateTime existingEnd, LocalDateTime requestedStart, LocalDateTime requestedEnd) {
        return !(requestedEnd.isBefore(existingStart) || requestedStart.isAfter(existingEnd) || requestedEnd.equals(existingStart) || requestedStart.equals(existingEnd));
    }

    @Override
    public List<MeetingRoomsResponseDTO> getAllMeetingRooms() {
        return meetingRoomsDao.findAllActiveRooms().stream()
                .map(room -> new MeetingRoomsResponseDTO(room.getId(), room.getName(), room.getCapacity()))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteRoom(Long id, String token) {
        String role = jwtUtil.extractRole(token);

        if (!"ADMIN".equals(role)) {
            throw new AccessDeniedException("Access Denied! Only Admins can delete meeting rooms.");
        }

        meetingRoomsDao.softDelete(id);
    }
}
