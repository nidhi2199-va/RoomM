package com.meeting.room.dao;

import com.meeting.room.model.MeetingRooms;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MeetingRoomsDao {
    MeetingRooms save(MeetingRooms meetingRoom);
    Optional<MeetingRooms> findById(Long id);
     List<MeetingRooms> findAll();
    MeetingRooms findByName(String name);
    Optional<MeetingRooms> findAvailableRoom(LocalDateTime startTime, LocalDateTime endTime, int minCapacity);
    void softDelete(Long roomId);
    List<MeetingRooms> findAllActiveRooms();  // Custom method for active rooms

}