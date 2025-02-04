package com.MeetingRoom.RoomM.dao;

import com.MeetingRoom.RoomM.Enums.BookingStatus;
import com.MeetingRoom.RoomM.model.Bookings;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingsDao {

    // Save a booking (either persist or merge based on the ID)
    Bookings save(Bookings booking);

    // Find a booking by its ID
    Optional<Bookings> findById(Long id);

    // Delete a booking by its ID
    void deleteById(Long id);

    // Check for overlapping bookings (Prevents double booking)
    long countOverlappingBookings(Long roomId, LocalDateTime startTime, LocalDateTime endTime);

    // Fetch all bookings for a specific user
    List<Bookings> findByUserId(Long userId);

    // Fetch all bookings for a specific meeting room
    List<Bookings> findByMeetingRoomId(Long roomId);

    // Fetch all bookings with a specific status
    List<Bookings> findByStatus(BookingStatus status);

    // Fetch all bookings within a specific time range
    List<Bookings> findBookingsWithinTimeRange(LocalDateTime startTime, LocalDateTime endTime);
    //List<Bookings> findByMeetingRoomAndStatus(Long roomId, BookingStatus status);

}
