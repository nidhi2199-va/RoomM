package com.MeetingRoom.RoomM.controller;

import com.MeetingRoom.RoomM.dao.MeetingRoomsDao;
import com.MeetingRoom.RoomM.dto.*;
import com.MeetingRoom.RoomM.model.Bookings;
import com.MeetingRoom.RoomM.model.MeetingRooms;
import com.MeetingRoom.RoomM.service.MeetingRoomsService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/meeting-rooms")
public class MeetingRoomsController {

    private final MeetingRoomsService meetingRoomsService;
    //private final MeetingRoomsDao meetingRoomsDao;

    public MeetingRoomsController(MeetingRoomsService meetingRoomsService, MeetingRoomsDao meetingRoomsDao) {
        this.meetingRoomsService = meetingRoomsService;
      //  this.meetingRoomsDao = meetingRoomsDao;
    }

    // Endpoint to add a meeting room (Only Admins)
    @PostMapping("/room")
    public ResponseEntity<MeetingRooms> addMeetingRoom(
            @RequestBody AddRoomRequestDTO addRoomRequestDTO,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        // Remove "Bearer " prefix from token
        String jwtToken = token.substring(7);

        MeetingRooms meetingRoom = meetingRoomsService.addMeetingRoom(addRoomRequestDTO, jwtToken);
        return ResponseEntity.ok(meetingRoom);
    }
    @PutMapping("/room/{roomId}")
    public ResponseEntity<MeetingRooms> updateMeetingRoom(
            @PathVariable Long roomId,
            @RequestBody UpdateRoomRequestDTO updateRoomRequestDTO,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        // Remove "Bearer " prefix from token
        String jwtToken = token.substring(7);

        // Call service to update the room
        MeetingRooms updatedRoom = meetingRoomsService.updateMeetingRoom(roomId, updateRoomRequestDTO, jwtToken);

        // Return updated room details
        return ResponseEntity.ok(updatedRoom);
    }
    @GetMapping("/{roomId}/time-slots")
    public RoomWithTimeSlotsDTO getRoomWithTimeSlots(@PathVariable Long roomId) {
        return meetingRoomsService.getRoomWithTimeSlots(roomId);
    }
    @GetMapping("/availability")
    public RoomAvailabilityDTO getAvailableRooms(
            @RequestParam("startTime") String startTime,
            @RequestParam("endTime") String endTime) {

        LocalDateTime requestedStart = LocalDateTime.parse(startTime);
        LocalDateTime requestedEnd = LocalDateTime.parse(endTime);

        return meetingRoomsService.getAvailableRoomsForTimeslot(requestedStart, requestedEnd);
    }
    @GetMapping("/all")
    public ResponseEntity<List<MeetingRoomsResponseDTO>> getAllMeetingRooms() {
        List<MeetingRoomsResponseDTO> meetingRooms = meetingRoomsService.getAllMeetingRooms();
        return ResponseEntity.ok(meetingRooms);
    }
    }

