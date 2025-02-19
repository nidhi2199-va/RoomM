package com.MeetingRoom.RoomM.controller;

import com.MeetingRoom.RoomM.Exceptions.ResourceNotFoundException;
import com.MeetingRoom.RoomM.Exceptions.RoomAlreadyExistsException;
import com.MeetingRoom.RoomM.dao.MeetingRoomsDao;
import com.MeetingRoom.RoomM.dto.*;
import com.MeetingRoom.RoomM.model.Bookings;
import com.MeetingRoom.RoomM.model.MeetingRooms;
import com.MeetingRoom.RoomM.service.MeetingRoomsService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
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
    @PostMapping("/room")
    public ResponseEntity<MeetingRooms> addMeetingRoom(
            @RequestBody AddRoomRequestDTO addRoomRequestDTO,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        String jwtToken = token.substring(7);
        MeetingRooms meetingRoom = meetingRoomsService.addMeetingRoom(addRoomRequestDTO, jwtToken);
        return ResponseEntity.ok(meetingRoom);
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

    @PutMapping("/{roomId}")
    public ResponseEntity<UpdateRoomResponseDTO> updateMeetingRoom(
            @PathVariable Long roomId,
            @RequestBody UpdateRoomRequestDTO updateRoomRequestDTO,
            @RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.substring(7);
            MeetingRooms updatedRoom = meetingRoomsService.updateMeetingRoom(roomId, updateRoomRequestDTO, jwtToken);
            UpdateRoomResponseDTO responseDTO = new UpdateRoomResponseDTO(
                    updatedRoom.getId(),
                    updatedRoom.getName(),
                    updatedRoom.getCapacity()
            );
            return ResponseEntity.ok(responseDTO);
        } catch (RoomAlreadyExistsException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @DeleteMapping
    public ResponseEntity<DeleteMeetingRoomDTO> deleteRoom(
            @RequestHeader("Authorization") String token,
            @RequestBody DeleteMeetingRoomRequestDTO request) {
        String jwtToken = token.substring(7);
        meetingRoomsService.deleteRoom(request.getRoomId(), jwtToken);

        DeleteMeetingRoomDTO response = new DeleteMeetingRoomDTO("Room deleted successfully");
        return ResponseEntity.ok(response);
    }

}