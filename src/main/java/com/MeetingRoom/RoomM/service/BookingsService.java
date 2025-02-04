package com.MeetingRoom.RoomM.service;

import com.MeetingRoom.RoomM.Enums.BookingStatus;
import com.MeetingRoom.RoomM.dao.BookingsDao;
import com.MeetingRoom.RoomM.dao.MeetingRoomsDao;
import com.MeetingRoom.RoomM.dao.UserDao;
import com.MeetingRoom.RoomM.dto.*;
import com.MeetingRoom.RoomM.model.Bookings;
import com.MeetingRoom.RoomM.model.MeetingRooms;
import com.MeetingRoom.RoomM.model.Users;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import lombok.extern.slf4j.Slf4j;



import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingsService {

    private final BookingsDao bookingRepository;
    private final MeetingRoomsDao meetingRoomRepository;
    private final UserDao userRepository;
    private final BookingsDao bookingsDao;

    // Create a new booking and resolve time slot conflict
    public BookingResponseDTO createBooking(BookingRequestDTO bookingRequestDTO) {
        // Check if the room is already booked in the given time slot
        long overlappingBookings = bookingRepository.countOverlappingBookings(bookingRequestDTO.getRoomId(),
                bookingRequestDTO.getStartTime(), bookingRequestDTO.getEndTime());

        // If there's an overlap, throw  a conflict exception (HTTP 409 Conflict)
        if (overlappingBookings > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Room is already booked for the selected time slot!");
        }

        // Retrieve the MeetingRoom and User by ID
        MeetingRooms meetingRoom = meetingRoomRepository.findById(bookingRequestDTO.getRoomId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found!"));

        Users user = userRepository.findById(bookingRequestDTO.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!"));

        // Create a new booking
        Bookings booking = new Bookings();
        booking.setRoom(meetingRoom);
        booking.setUser(user);
        booking.setStartTime(bookingRequestDTO.getStartTime());
        booking.setEndTime(bookingRequestDTO.getEndTime());
        booking.setStatus(BookingStatus.BOOKED);  // Default to confirmed

        // Save the booking in the repository
        Bookings savedBooking = bookingRepository.save(booking);

        // Return the response DTO
        return new BookingResponseDTO(
                savedBooking.getBookingId(),
                savedBooking.getUser().getId(),
                savedBooking.getRoom().getId(),
                savedBooking.getRoom().getName(),
                savedBooking.getUser().getName(),
                savedBooking.getStartTime(),
                savedBooking.getEndTime(),
                savedBooking.getStatus()
        );
    }


    public UpdateBookingResponseDTO updateBooking(Long bookingId, UpdateBookingRequestDTO updateBookingRequestDTO) {
        // Fetch booking
        Bookings booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found!"));

        log.info("Received update request: RoomId={}, StartTime={}, EndTime={}",
                updateBookingRequestDTO.getRoomId(), updateBookingRequestDTO.getStartTime(), updateBookingRequestDTO.getEndTime());

        // Check for overlapping bookings
        long overlappingBookings = bookingRepository.countOverlappingBookings(
                updateBookingRequestDTO.getRoomId(),
                updateBookingRequestDTO.getStartTime(),
                updateBookingRequestDTO.getEndTime()
        );

        if (overlappingBookings > 0) {
            throw new RuntimeException("Room is already booked for the selected time slot!");
        }

        // ✅ Update Room if Changed
        if (!booking.getRoom().getId().equals(updateBookingRequestDTO.getRoomId())) {
            MeetingRooms newRoom = meetingRoomRepository.findById(updateBookingRequestDTO.getRoomId())
                    .orElseThrow(() -> new RuntimeException("Room not found!"));
            booking.setRoom(newRoom);
        }

        // ✅ Log Before Updating Time
        log.info("Before update: StartTime={}, EndTime={}", booking.getStartTime(), booking.getEndTime());

        // ✅ Update Time (Fix)
        booking.setStartTime(updateBookingRequestDTO.getStartTime());
        booking.setEndTime(updateBookingRequestDTO.getEndTime());

        log.info("After update: StartTime={}, EndTime={}", booking.getStartTime(), booking.getEndTime());

        // ✅ Save Updated Booking
        Bookings updatedBooking = bookingRepository.save(booking);

        return new UpdateBookingResponseDTO(
                updatedBooking.getBookingId(),
                updatedBooking.getUser().getId(),
                updatedBooking.getRoom().getId(),
                updatedBooking.getRoom().getName(),
                updatedBooking.getUser().getName(),
                updatedBooking.getStartTime(),
                updatedBooking.getEndTime(),
                updatedBooking.getStatus()
        );
    }
    @Transactional
    public CancelBookingResponseDTO cancelBooking(CancelBookingRequestDTO requestDTO) {
        // Check if the booking exists
        Bookings booking = bookingRepository.findById(requestDTO.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Check if the user is the one who made the booking or if the user has permission
        if (!booking.getUser().getId().equals(requestDTO.getUserId())) {
            throw new RuntimeException("You do not have permission to cancel this booking");
        }

        // Check if the room ID matches the booking room ID
        if (!booking.getRoom().getId().equals(requestDTO.getRoomId())) {
            throw new RuntimeException("Room ID does not match with the booking");
        }

        // Check if the timeslot matches
        if (!booking.getStartTime().equals(requestDTO.getStartTime()) || !booking.getEndTime().equals(requestDTO.getEndTime())) {
            throw new RuntimeException("Time slot does not match with the booking");
        }

        // Cancel the booking by changing its status to CANCELED
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // Return success response
        return new CancelBookingResponseDTO(booking.getBookingId(), "Booking has been successfully canceled!");
    }



    public void updateBookingsToCompleted() {
        // Get current time
        LocalDateTime currentTime = LocalDateTime.now();

        // Find all confirmed bookings that have ended
        List<Bookings> bookings = bookingsDao.findByStatusAndEndTimeBefore(BookingStatus.COMPLETED, currentTime);

        // Iterate through the bookings and update their status to COMPLETED
        for (Bookings booking : bookings) {
            booking.setStatus(BookingStatus.COMPLETED);
            bookingsDao.save(booking);  // Save the updated booking
        }
    }

}