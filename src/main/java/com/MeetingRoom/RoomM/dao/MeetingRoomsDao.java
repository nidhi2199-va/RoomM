package com.MeetingRoom.RoomM.dao;

import com.MeetingRoom.RoomM.model.MeetingRooms;

import java.util.List;
import java.util.Optional;

public interface MeetingRoomsDao {

    MeetingRooms save(MeetingRooms room);

    Optional<MeetingRooms> findById(Long roomId);

    List<MeetingRooms> findAll();

    void deleteById(Long roomId);

    Optional<MeetingRooms> findByName(String roomName);
}
