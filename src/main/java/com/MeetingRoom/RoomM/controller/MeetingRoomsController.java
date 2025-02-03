package com.MeetingRoom.RoomM.controller;

import com.MeetingRoom.RoomM.dto.AddRoomRequestDTO;
import com.MeetingRoom.RoomM.model.MeetingRooms;
import com.MeetingRoom.RoomM.service.MeetingRoomsService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/meeting-rooms")
public class MeetingRoomsController {

    private final MeetingRoomsService meetingRoomsService;

    public MeetingRoomsController(MeetingRoomsService meetingRoomsService) {
        this.meetingRoomsService = meetingRoomsService;
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
}
