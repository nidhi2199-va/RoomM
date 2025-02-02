package com.MeetingRoom.RoomM.controller;

import com.MeetingRoom.RoomM.dto.AddRoomRequestDTO;
import com.MeetingRoom.RoomM.model.MeetingRooms;
import com.MeetingRoom.RoomM.service.MeetingRoomsService;
import com.MeetingRoom.RoomM.Utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/meeting-room")
public class MeetingRoomsController {

    private final MeetingRoomsService meetingRoomsService;
    private final JwtUtil jwtUtil;

    // Constructor injection for service and JwtUtil
    public MeetingRoomsController(MeetingRoomsService meetingRoomsService, JwtUtil jwtUtil) {
        this.meetingRoomsService = meetingRoomsService;
        this.jwtUtil = jwtUtil;
    }

    // API to add a meeting room
    @PostMapping("/add")
    public ResponseEntity<MeetingRooms> addMeetingRoom(@RequestHeader("Authorization") String token,
                                                       @RequestBody AddRoomRequestDTO addRoomRequestDTO) {
        try {
            // Extract email and role from the JWT token
            String email = jwtUtil.extractEmail(token.substring(7)); // Remove "Bearer " from token
            String role = jwtUtil.extractRole(token.substring(7));  // Extract the role from the token

            // Check if the role is ADMIN
            if (!role.equals("ADMIN")) {
                return ResponseEntity.status(403).body(null);  // Forbidden if user is not admin
            }

            // Proceed to add the meeting room if the user is admin
            MeetingRooms meetingRoom = meetingRoomsService.addMeetingRoom(addRoomRequestDTO);
            return ResponseEntity.status(201).body(meetingRoom);  // Created response with the new meeting room

        } catch (Exception e) {
            // Handle any errors (e.g., invalid token or role extraction failure)
            return ResponseEntity.status(403).body(null);  // Forbidden if token is invalid
        }
    }
}
