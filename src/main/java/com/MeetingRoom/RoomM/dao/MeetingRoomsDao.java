package com.MeetingRoom.RoomM.dao;

import com.MeetingRoom.RoomM.model.MeetingRooms;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MeetingRoomsDao {
    MeetingRooms save(MeetingRooms meetingRoom);
    Optional<MeetingRooms> findById(Long id);
     List<MeetingRooms> findAll();
    void deleteById(Long id);
    MeetingRooms findByName(String name);
    Optional<MeetingRooms> findAvailableRoom(LocalDateTime startTime, LocalDateTime endTime, int minCapacity);
}