package com.MeetingRoom.RoomM.service;

import com.MeetingRoom.RoomM.dao.MeetingRoomsDao;
import com.MeetingRoom.RoomM.dto.AddRoomRequestDTO;
import com.MeetingRoom.RoomM.model.MeetingRooms;
import org.springframework.stereotype.Service;

@Service
public class MeetingRoomsService {

    private final MeetingRoomsDao meetingRoomsDao;

    // Constructor injection for DAO
    public MeetingRoomsService(MeetingRoomsDao meetingRoomsDao) {
        this.meetingRoomsDao = meetingRoomsDao;
    }

    // Add Meeting Room Method (Modified to only accept AddRoomRequestDTO)
    public MeetingRooms addMeetingRoom(AddRoomRequestDTO addRoomRequestDTO) {
        // Create a new MeetingRooms entity from the DTO
        MeetingRooms meetingRoom = new MeetingRooms();
        meetingRoom.setName(addRoomRequestDTO.getName());
        meetingRoom.setCapacity(addRoomRequestDTO.getCapacity());
      //  meetingRoom.setEquipmentList(addRoomRequestDTO.getEquipmentList());
        meetingRoom.setIsAvailable(addRoomRequestDTO.getIsAvailable());

        // Save the new room in the database
        return meetingRoomsDao.save(meetingRoom);
    }
}
