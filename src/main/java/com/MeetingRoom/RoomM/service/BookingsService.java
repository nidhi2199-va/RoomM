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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        // Retrieve the MeetingRoom by ID

        MeetingRooms meetingRoom = meetingRoomRepository.findById(bookingRequestDTO.getRoomId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found!"));

        // Retrieve the User by ID
        Users user = userRepository.findById(bookingRequestDTO.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!"));

        // Check if the room has an active booking with status BOOKED
        List<Bookings> activeBookings = bookingRepository.findByRoomAndStatus(meetingRoom, BookingStatus.BOOKED);
        if (!activeBookings.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Room is already booked and cannot be reserved!");
        }

        // Create a new booking
        Bookings booking = new Bookings();
        booking.setRoom(meetingRoom);
        booking.setUser(user);
        booking.setStartTime(bookingRequestDTO.getStartTime());
        booking.setEndTime(bookingRequestDTO.getEndTime());
        booking.setStatus(BookingStatus.BOOKED);  // Default status

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

    @Transactional
    public UpdateBookingResponseDTO updateBooking(Long bookingId, UpdateBookingRequestDTO updateBookingRequestDTO) {
        // Fetch the existing booking
        Bookings booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found!"));

        log.info("Received update request: RoomId={}, StartTime={}, EndTime={}",
                updateBookingRequestDTO.getRoomId(), updateBookingRequestDTO.getStartTime(), updateBookingRequestDTO.getEndTime());

        // Check for overlapping bookings for the new room and time slot
        long overlappingBookings = bookingRepository.countOverlappingBookings(
                updateBookingRequestDTO.getRoomId(),
                updateBookingRequestDTO.getStartTime(),
                updateBookingRequestDTO.getEndTime()
        );

        // If there's an overlap, throw an exception
        if (overlappingBookings > 0) {
            throw new RuntimeException("Room is already booked for the selected time slot!");
        }

        // Update the room if it has changed
        if (!booking.getRoom().getId().equals(updateBookingRequestDTO.getRoomId())) {
            MeetingRooms newRoom = meetingRoomRepository.findById(updateBookingRequestDTO.getRoomId())
                    .orElseThrow(() -> new RuntimeException("Room not found!"));
            booking.setRoom(newRoom);
        }

        // Update the start and end time
        booking.setStartTime(updateBookingRequestDTO.getStartTime());
        booking.setEndTime(updateBookingRequestDTO.getEndTime());

        // Save the updated booking
        Bookings updatedBooking = bookingRepository.save(booking);

        // Return the updated booking response DTO
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



    @Transactional
    public boolean completeBooking(Long bookingId) {
        Optional<Bookings> optionalBooking = bookingsDao.findById(bookingId);

        if (optionalBooking.isPresent()) {
            Bookings booking = optionalBooking.get();

            // Ensure the booking is currently CONFIRMED before updating
            if (booking.getStatus() == BookingStatus.BOOKED) {
                booking.setStatus(BookingStatus.COMPLETED);
                bookingsDao.save(booking);
                return true;
            }
        }

        return false; // Booking not found or already completed
    }
    // Retrieve all booking history based on status (Completed, Cancelled)
    public List<BookingResponseDTO> getAllBookingHistory(List<BookingStatus> statuses) {
        return bookingsDao.findByStatusIn(statuses).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BookingResponseDTO> getUserBookingHistory(Long userId) {
        List<BookingStatus> statuses = Arrays.asList(BookingStatus.COMPLETED, BookingStatus.CANCELLED);
        List<Bookings> bookings = bookingsDao.findByUserIdAndStatusIn(userId, statuses);

        if (bookings.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No booking history found for this user!");
        }

        return bookings.stream().map(booking -> new BookingResponseDTO(
                booking.getBookingId(),
                booking.getUser().getId(),
                booking.getRoom().getId(),
                booking.getRoom().getName(),
                booking.getUser().getName(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getStatus()
        )).collect(Collectors.toList());
    }

    // Retrieve booking history by room ID
    public List<BookingResponseDTO> getRoomBookingHistory(Long roomId, List<BookingStatus> statuses) {
        return bookingsDao.findByRoomIdAndStatusIn(roomId, statuses).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Convert entity to DTO
    private BookingResponseDTO convertToDTO(Bookings booking) {
        return new BookingResponseDTO(
                booking.getBookingId(),
                booking.getUser().getId(),
                booking.getRoom().getId(),
                booking.getRoom().getName(),
                booking.getUser().getName(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getStatus()
        );
    }
}