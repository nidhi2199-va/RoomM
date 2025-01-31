package com.MeetingRoom.RoomM.controller;

import com.MeetingRoom.RoomM.model.MeetingRooms;

import com.MeetingRoom.RoomM.service.MeetingRoomsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rooms")
public class MeetingRoomsController {

    private final MeetingRoomsService meetingRoomsService;

    @Autowired
    public MeetingRoomsController(MeetingRoomsService meetingRoomService) {
        this.meetingRoomsService = meetingRoomService;
    }

    /**
     * Endpoint to add a new meeting room.
     *
     * @param room The meeting room to be added.
     * @return ResponseEntity with the saved meeting room and HTTP status.
     */
    @PostMapping("/add")
    public ResponseEntity<MeetingRooms> addRoom(@RequestBody MeetingRooms room) {
        try {
            // Call service method to add the room
            MeetingRooms savedRoom = meetingRoomsService.addRoom(room);
            return new ResponseEntity<>(savedRoom, HttpStatus.CREATED);  // 201 - Created
        } catch (IllegalArgumentException e) {
            // Handle invalid input (e.g., missing room name)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);  // 400 - Bad Request
        }
    }
}
