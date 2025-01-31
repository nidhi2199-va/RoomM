package com.MeetingRoom.RoomM.service;

import com.MeetingRoom.RoomM.model.MeetingRooms;
import com.MeetingRoom.RoomM.dao.MeetingRoomsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MeetingRoomsService {

    private final MeetingRoomsDao meetingRoomsDao;

    @Autowired
    public MeetingRoomsService(MeetingRoomsDao meetingRoomRepository) {
        this.meetingRoomsDao = meetingRoomRepository;
    }

    /**
     * Method to add a new room to the system.
     *
     * @param room The meeting room to be added.
     * @return The saved meeting room entity.
     */
    public MeetingRooms addRoom(MeetingRooms room) {
        // Perform any necessary validation (e.g., check if the room name is unique)
        if (room.getName() == null || room.getName().isEmpty()) {
            throw new IllegalArgumentException("Room name cannot be empty");
        }

        // Save the room to the database
        return meetingRoomsDao.save(room);
    }
}
