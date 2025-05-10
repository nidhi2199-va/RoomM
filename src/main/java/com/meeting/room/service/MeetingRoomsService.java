//package com.MeetingRoom.RoomM.service;
//
//import com.MeetingRoom.RoomM.Enums.BookingStatus;
//import com.MeetingRoom.RoomM.Enums.Role;
//import com.MeetingRoom.RoomM.Exceptions.AccessDeniedException;
//import com.MeetingRoom.RoomM.Exceptions.BadRequestException;
//import com.MeetingRoom.RoomM.Exceptions.ResourceNotFoundException;
//import com.MeetingRoom.RoomM.Exceptions.RoomAlreadyExistsException;
//import com.MeetingRoom.RoomM.dao.BookingsDao;
//import com.MeetingRoom.RoomM.dao.MeetingRoomsDao;
//import com.MeetingRoom.RoomM.dto.*;
//import com.MeetingRoom.RoomM.model.Bookings;
//import com.MeetingRoom.RoomM.model.MeetingRooms;
//import com.MeetingRoom.RoomM.Utils.JwtUtil;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Service
//public class MeetingRoomsService {
//
//    private final MeetingRoomsDao meetingRoomsDao;
//    private final BookingsDao bookingsDao;
//    private final JwtUtil jwtUtil;
//
//    // Constructor injection for DAO and JWT Utility
//    public MeetingRoomsService(MeetingRoomsDao meetingRoomsDao, BookingsDao bookingsDao, JwtUtil jwtUtil) {
//        this.meetingRoomsDao = meetingRoomsDao;
//        this.bookingsDao = bookingsDao;
//        this.jwtUtil = jwtUtil;
//    }
//public MeetingRooms addMeetingRoom(AddRoomRequestDTO addRoomRequestDTO, String token) {
//    try {
//        String role = jwtUtil.extractRole(token);
//
//        if (!"ADMIN".equals(role)) {
//            System.out.println(role);
//            throw new RuntimeException("Access Denied! Only Admins can add meeting rooms.");
//        }
//        MeetingRooms existingRoom = meetingRoomsDao.findByName(addRoomRequestDTO.getName());
//        if (existingRoom != null) {
//            throw new RoomAlreadyExistsException("Room name already exists.");
//        }
//        MeetingRooms meetingRoom = new MeetingRooms();
//        meetingRoom.setName(addRoomRequestDTO.getName());
//        meetingRoom.setCapacity(addRoomRequestDTO.getCapacity());
//        return meetingRoomsDao.save(meetingRoom);
//    } catch (Exception e) {
//        throw new RuntimeException("Failed to add meeting room: " + e.getMessage(), e);
//    }
//}
//    public MeetingRooms updateMeetingRoom(Long roomId, UpdateRoomRequestDTO updateRoomRequestDTO, String token) {
//        try {
//            String role = jwtUtil.extractRole(token);
//            if (!"ADMIN".equals(role)) {
//                throw new RuntimeException("Access Denied! Only Admins can update meeting rooms.");
//            }
//            MeetingRooms existingRoom = meetingRoomsDao.findById(roomId)
//                    .orElseThrow(() -> new ResourceNotFoundException("Meeting room not found"));
//            if (!existingRoom.getName().equals(updateRoomRequestDTO.getName())) {
//                MeetingRooms roomWithSameName = meetingRoomsDao.findByName(updateRoomRequestDTO.getName());
//                if (roomWithSameName != null) {
//                    throw new RoomAlreadyExistsException("Room name already exists.");
//                }
//            }
//            existingRoom.setName(updateRoomRequestDTO.getName());
//            existingRoom.setCapacity(updateRoomRequestDTO.getCapacity());
//            return meetingRoomsDao.save(existingRoom);
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to update meeting room: " + e.getMessage(), e);
//        }
//    }
//public List<MeetingRoomDTO> getAllMeetingRooms(String token) {
//    String role = jwtUtil.extractRole(token);
//    if (!"ADMIN".equals(role)) {
//        throw new RuntimeException("Access Denied! Only Admins can view meeting rooms.");
//    }
//    List<MeetingRooms> meetingRooms = meetingRoomsDao.findAll();
//    return meetingRooms.stream()
//            .map(this::mapToDTO)
//            .collect(Collectors.toList());
//}
//    private MeetingRoomDTO mapToDTO(MeetingRooms meetingRoom) {
//        MeetingRoomDTO dto = new MeetingRoomDTO();
//        dto.setName(meetingRoom.getName());
//        dto.setCapacity(meetingRoom.getCapacity());
//        return dto;
//    }
//    public RoomWithTimeSlotsDTO getRoomWithTimeSlots(Long roomId) {
//        Optional<MeetingRooms> roomOptional = meetingRoomsDao.findById(roomId);
//        if (roomOptional.isEmpty()) {
//            throw new RuntimeException("Room not found");
//        }
//        MeetingRooms room = roomOptional.get();
//        List<Bookings> bookings = bookingsDao.findByMeetingRoomId(roomId);
//        List<TimeSlotDTO> timeSlots = bookings.stream().map(booking -> {
//            TimeSlotDTO dto = new TimeSlotDTO();
//            dto.setStartTime(booking.getStartTime());
//            dto.setEndTime(booking.getEndTime());
//            return dto;
//        }).collect(Collectors.toList());
//        RoomWithTimeSlotsDTO response = new RoomWithTimeSlotsDTO();
//        response.setRoomId(room.getId());
//        response.setName(room.getName());
//        response.setCapacity(room.getCapacity());
//        response.setTimeSlots(timeSlots);
//
//        return response;
//    }
//    public RoomAvailabilityDTO getAvailableRoomsForTimeslot(LocalDateTime requestedStart, LocalDateTime requestedEnd) {
//        List<MeetingRooms> allRooms = meetingRoomsDao.findAllActiveRooms();
//        List<Long> availableRoomIds = allRooms.stream()
//                .map(MeetingRooms::getId)
//                .filter(id -> isRoomAvailable(id, requestedStart, requestedEnd))
//                .collect(Collectors.toList());
//        return new RoomAvailabilityDTO(availableRoomIds);
//    }
//    public boolean isRoomAvailable(Long roomId, LocalDateTime requestedStart, LocalDateTime requestedEnd) {
//        Optional<MeetingRooms> roomOptional = meetingRoomsDao.findById(roomId);
//        if (roomOptional.isEmpty()) {
//            throw new RuntimeException("Room not found");
//        }
//        MeetingRooms room = roomOptional.get();
//        List<Bookings> activeBookings = bookingsDao.findByMeetingRoomId(roomId)
//                .stream()
//                .filter(booking -> booking.getStatus() == BookingStatus.BOOKED) // Only check active bookings
//                .toList();
//
//        for (Bookings booking : activeBookings) {
//            if (isOverlapping(booking.getStartTime(), booking.getEndTime(), requestedStart, requestedEnd)) {
//                return false;
//            }
//        }
//
//        return true;
//    }
//    private boolean isOverlapping(LocalDateTime existingStart, LocalDateTime existingEnd, LocalDateTime requestedStart, LocalDateTime requestedEnd) {
//        return !(requestedEnd.isBefore(existingStart) || requestedStart.isAfter(existingEnd) || requestedEnd.equals(existingStart) || requestedStart.equals(existingEnd));
//    }
//    public List<MeetingRoomsResponseDTO> getAllMeetingRooms() {
//        List<MeetingRooms> allRooms = meetingRoomsDao.findAllActiveRooms();
//
//        return allRooms.stream()
//                .map(room -> new MeetingRoomsResponseDTO(room.getId(), room.getName(), room.getCapacity()))
//                .collect(Collectors.toList());
//    }
//
//    public void deleteRoom(Long id, String token) {
//        String role = jwtUtil.extractRole(token);
//        if (!"ADMIN".equals(role)) {
//            throw new AccessDeniedException("Access Denied! Only Admins can add meeting rooms.");
//        }
//        meetingRoomsDao.softDelete(id);
//    }
//
//}
package com.meeting.room.service;

import com.meeting.room.dto.*;
import com.meeting.room.model.MeetingRooms;

import java.time.LocalDateTime;
import java.util.List;

public interface MeetingRoomsService {
    MeetingRooms addMeetingRoom(AddRoomRequestDTO addRoomRequestDTO, String token);
    MeetingRooms updateMeetingRoom(Long roomId, UpdateRoomRequestDTO updateRoomRequestDTO, String token);
    List<MeetingRoomDTO> getAllMeetingRooms(String token);
    RoomWithTimeSlotsDTO getRoomWithTimeSlots(Long roomId);
    RoomAvailabilityDTO getAvailableRoomsForTimeslot(LocalDateTime requestedStart, LocalDateTime requestedEnd);
    boolean isRoomAvailable(Long roomId, LocalDateTime requestedStart, LocalDateTime requestedEnd);
    List<MeetingRoomsResponseDTO> getAllMeetingRooms();
    void deleteRoom(Long id, String token);
}
