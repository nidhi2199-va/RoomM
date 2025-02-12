package com.MeetingRoom.RoomM.service;

import com.MeetingRoom.RoomM.Enums.Role;
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
public class MeetingRoomsService {

    private final MeetingRoomsDao meetingRoomsDao;
    private final BookingsDao bookingsDao;
    private final JwtUtil jwtUtil;

    // Constructor injection for DAO and JWT Utility
    public MeetingRoomsService(MeetingRoomsDao meetingRoomsDao, BookingsDao bookingsDao, JwtUtil jwtUtil) {
        this.meetingRoomsDao = meetingRoomsDao;
        this.bookingsDao = bookingsDao;
        this.jwtUtil = jwtUtil;
    }

//    // Add Meeting Room - Only Admins can add rooms
//    public MeetingRooms addMeetingRoom(AddRoomRequestDTO addRoomRequestDTO, String token) {
//        // Extract user role from JWT
//        String role = jwtUtil.extractRole(token);
//
////         Allow only Admins to add rooms
//        if (!"ADMIN".equals(role)) {
//            System.out.println(role);
//            throw new RuntimeException("Access Denied! Only Admins can add meeting rooms.");
//        }
//
//        // Create a new MeetingRooms entity from DTO
//        MeetingRooms meetingRoom = new MeetingRooms();
//        meetingRoom.setName(addRoomRequestDTO.getName());
//        meetingRoom.setCapacity(addRoomRequestDTO.getCapacity());
//
//
//        // Save the new room in the database
//        return meetingRoomsDao.save(meetingRoom);
//    }
// Add Meeting Room - Only Admins can add rooms
public MeetingRooms addMeetingRoom(AddRoomRequestDTO addRoomRequestDTO, String token) {
    try {
        // Extract user role from JWT
        String role = jwtUtil.extractRole(token);

        // Allow only Admins to add rooms
        if (!"ADMIN".equals(role)) {
            System.out.println(role);
            throw new RuntimeException("Access Denied! Only Admins can add meeting rooms.");
        }

        // Check if a room with the same name already exists
        MeetingRooms existingRoom = meetingRoomsDao.findByName(addRoomRequestDTO.getName());
        if (existingRoom != null) {
            throw new RoomAlreadyExistsException("Room name already exists.");
        }
        // Create a new MeetingRooms entity from DTO
        MeetingRooms meetingRoom = new MeetingRooms();
        meetingRoom.setName(addRoomRequestDTO.getName());
        meetingRoom.setCapacity(addRoomRequestDTO.getCapacity());

        // Save the new room in the database
        return meetingRoomsDao.save(meetingRoom);
    } catch (Exception e) {
        // Log the exception and rethrow it
        e.printStackTrace(); // For debugging, you can replace with proper logging
        throw new RuntimeException("Failed to add meeting room: " + e.getMessage(), e);
    }
}


    public MeetingRooms updateMeetingRoom(Long roomId, UpdateRoomRequestDTO updateRoomRequestDTO, String token) {
        String role = jwtUtil.extractRole(token);
        if (!"ADMIN".equals(role)) {
            throw new RuntimeException("Access Denied! Only Admins can update meeting rooms.");
        }

        // Find the existing room by ID
        MeetingRooms existingRoom = meetingRoomsDao.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // Update the room fields
        existingRoom.setName(updateRoomRequestDTO.getName());
        existingRoom.setCapacity(updateRoomRequestDTO.getCapacity());
       // existingRoom.setIsAvailable(updateRoomRequestDTO.isIsAvailable());

        // Save the updated room back to the database
        return meetingRoomsDao.save(existingRoom);
    }

    // Get a room with all its time slots (bookings)
    public RoomWithTimeSlotsDTO getRoomWithTimeSlots(Long roomId) {
        // Fetch the meeting room from the database
        Optional<MeetingRooms> roomOptional = meetingRoomsDao.findById(roomId);
        if (roomOptional.isEmpty()) {
            throw new RuntimeException("Room not found");
        }

        MeetingRooms room = roomOptional.get();

        // Fetch all bookings (time slots) for the room
        List<Bookings> bookings = bookingsDao.findByMeetingRoomId(roomId);

        // Convert bookings to TimeSlotDTO
        List<TimeSlotDTO> timeSlots = bookings.stream().map(booking -> {
            TimeSlotDTO dto = new TimeSlotDTO();
            dto.setStartTime(booking.getStartTime());
            dto.setEndTime(booking.getEndTime());
            return dto;
        }).collect(Collectors.toList());

        // Create RoomWithTimeSlotsDTO response
        RoomWithTimeSlotsDTO response = new RoomWithTimeSlotsDTO();
        response.setRoomId(room.getId());
        response.setName(room.getName());
        response.setCapacity(room.getCapacity());
        response.setTimeSlots(timeSlots);

        return response;
    }
    public RoomAvailabilityDTO getAvailableRoomsForTimeslot(LocalDateTime requestedStart, LocalDateTime requestedEnd) {
        // Get all rooms
        List<MeetingRooms> allRooms = meetingRoomsDao.findAll();

        // Filter available rooms by checking against bookings
        List<Long> availableRoomIds = allRooms.stream()
                .map(MeetingRooms::getId)
                .filter(id -> isRoomAvailable(id, requestedStart, requestedEnd))
                .collect(Collectors.toList());

        // Return a list of available room IDs
        return new RoomAvailabilityDTO(availableRoomIds);
    }
    public boolean isRoomAvailable(Long roomId, LocalDateTime requestedStart, LocalDateTime requestedEnd) {
        // Fetch the meeting room by ID
        Optional<MeetingRooms> roomOptional = meetingRoomsDao.findById(roomId);
        if (roomOptional.isEmpty()) {
            throw new RuntimeException("Room not found");
        }

        MeetingRooms room = roomOptional.get();

        // Fetch all bookings for the room
        List<Bookings> bookings = bookingsDao.findByMeetingRoomId(roomId);

        // Check if the requested time slot overlaps with any existing booking
        for (Bookings booking : bookings) {
            // Check if the requested timeslot overlaps with any existing booking
            if (isOverlapping(booking.getStartTime(), booking.getEndTime(), requestedStart, requestedEnd)) {
                return false;  // Room is not available
            }
        }

        return true;  // Room is available
    }

    // Helper method to check if two time slots overlap
    private boolean isOverlapping(LocalDateTime existingStart, LocalDateTime existingEnd, LocalDateTime requestedStart, LocalDateTime requestedEnd) {
        return !(requestedEnd.isBefore(existingStart) || requestedStart.isAfter(existingEnd));
    }
    public List<MeetingRoomsResponseDTO> getAllMeetingRooms() {
        List<MeetingRooms> allRooms = meetingRoomsDao.findAll();

        return allRooms.stream()
                .map(room -> new MeetingRoomsResponseDTO(room.getId(), room.getName(), room.getCapacity()))
                .collect(Collectors.toList());
    }
}
