package com.MeetingRoom.RoomM.service;

import com.MeetingRoom.RoomM.Enums.Role;
import com.MeetingRoom.RoomM.dao.MeetingRoomsDao;
import com.MeetingRoom.RoomM.dto.AddRoomRequestDTO;
import com.MeetingRoom.RoomM.model.MeetingRooms;
import com.MeetingRoom.RoomM.Utils.JwtUtil;
import org.springframework.stereotype.Service;

@Service
public class MeetingRoomsService {

    private final MeetingRoomsDao meetingRoomsDao;
    private final JwtUtil jwtUtil;

    // Constructor injection for DAO and JWT Utility
    public MeetingRoomsService(MeetingRoomsDao meetingRoomsDao, JwtUtil jwtUtil) {
        this.meetingRoomsDao = meetingRoomsDao;
        this.jwtUtil = jwtUtil;
    }

    // Add Meeting Room - Only Admins can add rooms
    public MeetingRooms addMeetingRoom(AddRoomRequestDTO addRoomRequestDTO, String token) {
        // Extract user role from JWT
        String role = jwtUtil.extractRole(token);

//         Allow only Admins to add rooms
        if (!"ADMIN".equals(role)) {
            System.out.println(role);
            throw new RuntimeException("Access Denied! Only Admins can add meeting rooms.");
        }

        // Create a new MeetingRooms entity from DTO
        MeetingRooms meetingRoom = new MeetingRooms();
        meetingRoom.setName(addRoomRequestDTO.getName());
        meetingRoom.setCapacity(addRoomRequestDTO.getCapacity());
        meetingRoom.setIsAvailable(addRoomRequestDTO.getIsAvailable());

        // Save the new room in the database
        return meetingRoomsDao.save(meetingRoom);
    }
}
