package com.meeting.room.service;

import com.meeting.room.enums.BookingStatus;
import com.meeting.room.exceptions.BadRequestException;
import com.meeting.room.exceptions.ConflictException;
import com.meeting.room.exceptions.ResourceNotFoundException;
import com.meeting.room.utils.JwtUtil;
import com.meeting.room.dao.BookingsDao;
import com.meeting.room.dao.MeetingRoomsDao;
import com.meeting.room.dao.UserDao;
import com.meeting.room.dto.BookingRequestDTO;
import com.meeting.room.dto.BookingResponseDTO;
import com.meeting.room.dto.UpdateBookingRequestDTO;
import com.meeting.room.dto.UpdateBookingResponseDTO;
import com.meeting.room.model.Bookings;
import com.meeting.room.model.MeetingRooms;
import com.meeting.room.model.Users;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingsServiceImp implements BookingsService {

   // private static final Logger LOGGER = Logger.getLogger(BookingsServiceImp.class.getName());
    private final BookingsDao bookingRepository;
    private final MeetingRoomsDao meetingRoomRepository;
    private final UserDao userRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    @Override
    public BookingResponseDTO createBooking(BookingRequestDTO bookingRequestDTO, String token) {
        // Extract user from token
        String email = jwtUtil.extractEmail(token);
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!"));

        // Retrieve the meeting room
        MeetingRooms meetingRoom = meetingRoomRepository.findById(bookingRequestDTO.getRoomId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found!"));

        // Extract the booking date
        LocalDate bookingDate = bookingRequestDTO.getStartTime().toLocalDate();


        List<Bookings> activeBookings = bookingRepository.findByRoomAndStatus(meetingRoom, BookingStatus.BOOKED)
                .stream()
                .filter(b -> b.getStartTime().toLocalDate().equals(bookingDate))
                .filter(b -> !b.getEndTime().isBefore(LocalDateTime.now())) // Ignore past bookings
                .filter(b -> !bookingRequestDTO.getStartTime().isAfter(b.getEndTime()) &&
                        !bookingRequestDTO.getEndTime().isBefore(b.getStartTime()))
                .toList();

        // Prevent double booking
        if (!activeBookings.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Room is already booked for this time slot on the selected date!");
        }

        // Create new booking
        Bookings booking = new Bookings();
        booking.setRoom(meetingRoom);
        booking.setUser(user);
        booking.setStartTime(bookingRequestDTO.getStartTime());
        booking.setEndTime(bookingRequestDTO.getEndTime());
        booking.setStatus(BookingStatus.BOOKED);

        // Save the booking
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
    @Override
    public void cancelBooking(Long bookingId, String token) {
        String email = jwtUtil.extractEmail(token);
        Bookings booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to cancel this booking");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    @Transactional
    @Override
    public boolean completeBooking(Long bookingId) {
        Optional<Bookings> optionalBooking = bookingRepository.findById(bookingId);

        if (optionalBooking.isPresent()) {
            Bookings booking = optionalBooking.get();
            if (booking.getStatus() == BookingStatus.BOOKED) {
                booking.setStatus(BookingStatus.COMPLETED);
                bookingRepository.save(booking);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<BookingResponseDTO> getAllBookingHistory(List<BookingStatus> statuses) {
        return bookingRepository.findByStatusIn(statuses).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDTO> getUserBookingHistory(String token) {
        String email = jwtUtil.extractEmail(token);
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!"));

        List<BookingStatus> statuses = Arrays.asList(BookingStatus.COMPLETED, BookingStatus.BOOKED);
        List<Bookings> bookings = bookingRepository.findByUserIdAndStatusIn(user.getId(), statuses);
//        List<Bookings> complBooking =  new ArrayList<>();
//        complBooking = bookingRepository.findByUserIdAndTimeslot(user.getId(), System.currentTimeMillis());
//        bookings.addAll(complBooking);

        return bookings.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDTO> getRoomBookingHistory(Long roomId, List<BookingStatus> statuses) {
        return bookingRepository.findByRoomIdAndStatusIn(roomId, statuses).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UpdateBookingResponseDTO updateBooking(Long bookingId, UpdateBookingRequestDTO updateBookingRequestDTO) {
        Bookings booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found!"));

        if (!booking.getStatus().equals(BookingStatus.BOOKED)) {
            throw new BadRequestException("Only 'BOOKED' status bookings can be updated!");
        }

        long overlappingBookings = bookingRepository.countOverlappingBookingsExcludingCurrent(
                updateBookingRequestDTO.getRoomId(),
                updateBookingRequestDTO.getStartTime(),
                updateBookingRequestDTO.getEndTime(),
                bookingId
        );

        if (overlappingBookings > 0) {
            throw new ConflictException("The selected time slot conflicts with another booking!");
        }

        if (!booking.getRoom().getId().equals(updateBookingRequestDTO.getRoomId())) {
            MeetingRooms newRoom = meetingRoomRepository.findById(updateBookingRequestDTO.getRoomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found!"));
            booking.setRoom(newRoom);
        }

        booking.setStartTime(updateBookingRequestDTO.getStartTime());
        booking.setEndTime(updateBookingRequestDTO.getEndTime());
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
