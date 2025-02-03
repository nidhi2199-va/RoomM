package com.MeetingRoom.RoomM.dao;

import com.MeetingRoom.RoomM.model.MeetingRooms;
import java.util.List;
import java.util.Optional;

public interface MeetingRoomsDao {
    MeetingRooms save(MeetingRooms meetingRoom);
    Optional<MeetingRooms> findById(Long id);
   // List<MeetingRooms> findAll();
    void deleteById(Long id);
}
