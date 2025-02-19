package com.MeetingRoom.RoomM.service;

import com.MeetingRoom.RoomM.Enums.BookingStatus;
import com.MeetingRoom.RoomM.Exceptions.BadRequestException;
import com.MeetingRoom.RoomM.Exceptions.ConflictException;
import com.MeetingRoom.RoomM.Exceptions.ResourceNotFoundException;
import com.MeetingRoom.RoomM.Utils.JwtUtil;
import com.MeetingRoom.RoomM.dao.BookingsDao;
import com.MeetingRoom.RoomM.dao.MeetingRoomsDao;
import com.MeetingRoom.RoomM.dao.UserDao;
import com.MeetingRoom.RoomM.dto.*;
import com.MeetingRoom.RoomM.model.Bookings;
import com.MeetingRoom.RoomM.model.MeetingRooms;
import com.MeetingRoom.RoomM.model.Users;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingsServiceImp implements BookingsService {

    private static final Logger LOGGER = Logger.getLogger(BookingsServiceImp.class.getName());
    private final BookingsDao bookingRepository;
    private final MeetingRoomsDao meetingRoomRepository;
    private final UserDao userRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    @Override
    public BookingResponseDTO createBooking(BookingRequestDTO bookingRequestDTO, String token) {
        String email = jwtUtil.extractEmail(token);
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!"));
        MeetingRooms meetingRoom = meetingRoomRepository.findById(bookingRequestDTO.getRoomId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found!"));

        LocalDate bookingDate = bookingRequestDTO.getStartTime().toLocalDate();

        List<Bookings> activeBookings = bookingRepository.findByRoomAndStatus(meetingRoom, BookingStatus.BOOKED, BookingStatus.CANCELLED)
                .stream()
                .filter(b -> b.getStartTime().toLocalDate().equals(bookingDate))
                .filter(b -> !b.getEndTime().isBefore(LocalDateTime.now()))
                .filter(b -> bookingRequestDTO.getStartTime().isBefore(b.getEndTime()) &&
                        bookingRequestDTO.getEndTime().isAfter(b.getStartTime()))
                .toList();

        if (!activeBookings.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Room is already booked for this time slot on the selected date!");
        }

        Bookings booking = new Bookings();
        booking.setRoom(meetingRoom);
        booking.setUser(user);
        booking.setStartTime(bookingRequestDTO.getStartTime());
        booking.setEndTime(bookingRequestDTO.getEndTime());
        booking.setStatus(BookingStatus.BOOKED);

        Bookings savedBooking = bookingRepository.save(booking);

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

        List<BookingStatus> statuses = Arrays.asList(BookingStatus.COMPLETED, BookingStatus.CANCELLED, BookingStatus.BOOKED);
        List<Bookings> bookings = bookingRepository.findByUserIdAndStatusIn(user.getId(), statuses);

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
