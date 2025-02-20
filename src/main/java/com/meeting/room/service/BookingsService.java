//package com.MeetingRoom.RoomM.service;
//
//import com.MeetingRoom.RoomM.Enums.BookingStatus;
//import com.MeetingRoom.RoomM.Exceptions.BadRequestException;
//import com.MeetingRoom.RoomM.Exceptions.ConflictException;
//import com.MeetingRoom.RoomM.Exceptions.ResourceNotFoundException;
//import com.MeetingRoom.RoomM.Utils.JwtUtil;
//import com.MeetingRoom.RoomM.dao.BookingsDao;
//import com.MeetingRoom.RoomM.dao.MeetingRoomsDao;
//import com.MeetingRoom.RoomM.dao.UserDao;
//import com.MeetingRoom.RoomM.dto.*;
//import com.MeetingRoom.RoomM.model.Bookings;
//import com.MeetingRoom.RoomM.model.MeetingRooms;
//import com.MeetingRoom.RoomM.model.Users;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Service;
//
//import org.springframework.web.server.ResponseStatusException;
//import lombok.extern.slf4j.Slf4j;
//
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//import java.util.logging.Logger;
//import java.util.stream.Collectors;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class BookingsService {
//    private static final Logger LOGGER = Logger.getLogger(BookingsService.class.getName());
//    private final BookingsDao bookingRepository;
//    private final MeetingRoomsDao meetingRoomRepository;
//    private final UserDao userRepository;
//    private final BookingsDao bookingsDao;
//    private final JwtUtil jwtUtil;
//
//    @Transactional
//    public BookingResponseDTO createBooking(BookingRequestDTO bookingRequestDTO, String token) {
//        // Extract user email from JWT token
//        String email = jwtUtil.extractEmail(token);
//
//        // Retrieve the User from the database using email
//        Users user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!"));
//
//        // Retrieve the MeetingRoom by ID
//        MeetingRooms meetingRoom = meetingRoomRepository.findById(bookingRequestDTO.getRoomId())
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found!"));
//
//        // Extract the booking date from the requested start time
//        LocalDate bookingDate = bookingRequestDTO.getStartTime().toLocalDate();
//
////        // Fetch active bookings for the same room that are not COMPLETED
////        List<Bookings> activeBookings = bookingRepository.findByRoomAndStatus(meetingRoom, BookingStatus.BOOKED)
////                .stream()
////                .filter(b -> b.getStartTime().toLocalDate().equals(bookingDate)) // Ensure same date
////                .filter(b ->
////                        (bookingRequestDTO.getStartTime().isBefore(b.getEndTime()) &&
////                                bookingRequestDTO.getEndTime().isAfter(b.getStartTime())) // Time Overlaps
////                )
////                .toList();
////
////        if (!activeBookings.isEmpty()) {
////            throw new ResponseStatusException(HttpStatus.CONFLICT, "Room is already booked for this time slot on the selected date!");
////        }
////
////
////        // Create a new booking
////        Bookings booking = new Bookings();
////        booking.setRoom(meetingRoom);
////        booking.setUser(user);  // Set the user from the extracted email
////        booking.setStartTime(bookingRequestDTO.getStartTime());
////        booking.setEndTime(bookingRequestDTO.getEndTime());
////        booking.setStatus(BookingStatus.BOOKED);  // Default status
////
////        // Save the booking in the repository
////        Bookings savedBooking = bookingRepository.save(booking);
////
////        // Return the response DTO
////        return new BookingResponseDTO(
////                savedBooking.getBookingId(),
////                savedBooking.getUser().getId(),
////                savedBooking.getRoom().getId(),
////                savedBooking.getRoom().getName(),
////                savedBooking.getUser().getName(),
////                savedBooking.getStartTime(),
////                savedBooking.getEndTime(),
////                savedBooking.getStatus()
////        );
//        // Fetch active bookings for the same room that are BOOKED or CANCELED but not COMPLETED
////    List<Bookings> activeBookings = bookingRepository.findByRoomAndStatus(meetingRoom, BookingStatus.BOOKED)
////            .stream()
////            .filter(b -> b.getStatus() == BookingStatus.BOOKED || b.getStatus() == BookingStatus.CANCELLED) // Allow only BOOKED and CANCELLED
////            .filter(b -> b.getStartTime().toLocalDate().equals(bookingDate)) // Ensure same date
////            .filter(b -> !b.getEndTime().isBefore(LocalDateTime.now())) // Exclude past bookings
////            .filter(b ->
////                    bookingRequestDTO.getStartTime().isBefore(b.getEndTime()) &&
////                            bookingRequestDTO.getEndTime().isAfter(b.getStartTime()) // Time Overlaps
////            )
////            .toList();
//        List<Bookings> activeBookings = bookingRepository.findByRoomAndStatus(meetingRoom, BookingStatus.BOOKED, BookingStatus.CANCELLED)
//                .stream()
//                .filter(b -> b.getStartTime().toLocalDate().equals(bookingDate)) // Ensure same date
//                .filter(b -> !b.getEndTime().isBefore(LocalDateTime.now())) // Exclude past bookings
//                .filter(b ->
//                        bookingRequestDTO.getStartTime().isBefore(b.getEndTime()) && // New booking starts before existing booking ends
//                                bookingRequestDTO.getEndTime().isAfter(b.getStartTime())    // New booking ends after existing booking starts
//                )
//                .toList();
//
//        if (!activeBookings.isEmpty()) {
//            throw new ResponseStatusException(HttpStatus.CONFLICT, "Room is already booked for this time slot on the selected date!");
//        }
//
//// Create a new booking
//        Bookings booking = new Bookings();
//        booking.setRoom(meetingRoom);
//        booking.setUser(user);  // Set the user from the extracted email
//        booking.setStartTime(bookingRequestDTO.getStartTime());
//        booking.setEndTime(bookingRequestDTO.getEndTime());
//        booking.setStatus(BookingStatus.BOOKED);  // Default status
//
//// Save the booking in the repository
//        Bookings savedBooking = bookingRepository.save(booking);
//
//// Return the response DTO
//        return new BookingResponseDTO(
//                savedBooking.getBookingId(),
//                savedBooking.getUser().getId(),
//                savedBooking.getRoom().getId(),
//                savedBooking.getRoom().getName(),
//                savedBooking.getUser().getName(),
//                savedBooking.getStartTime(),
//                savedBooking.getEndTime(),
//                savedBooking.getStatus()
//        );
//    }
//
//
//    @Transactional
//    public void cancelBooking(Long bookingId, String token) {
//        // Extract email from JWT token
//        String email = jwtUtil.extractEmail(token);
//
//        // Find the booking by ID
//        Bookings booking = bookingsDao.findById(bookingId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
//
//        // Find the user by email
//        Users user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
//
//        // Check if the user is the owner of the booking
//        if (!booking.getUser().getId().equals(user.getId())) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to cancel this booking");
//        }
//
//        // Update booking status to 'CANCELLED'
//        booking.setStatus(BookingStatus.CANCELLED);
//        bookingsDao.save(booking);
//    }
//
//    @Transactional
//    public boolean completeBooking(Long bookingId) {
//        Optional<Bookings> optionalBooking = bookingsDao.findById(bookingId);
//
//        if (optionalBooking.isPresent()) {
//            Bookings booking = optionalBooking.get();
//
//            // Ensure the booking is currently CONFIRMED before updating
//            if (booking.getStatus() == BookingStatus.BOOKED) {
//                booking.setStatus(BookingStatus.COMPLETED);
//                bookingsDao.save(booking);
//                return true;
//            }
//        }
//
//        return false; // Booking not found or already completed
//    }
//
//    // Retrieve all booking history based on status (Completed, Cancelled)
//    public List<BookingResponseDTO> getAllBookingHistory(List<BookingStatus> statuses) {
//        return bookingsDao.findByStatusIn(statuses).stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
//    }
//
//    public List<BookingResponseDTO> getUserBookingHistory(String token) {
//
//        // Extract user email from JWT
//        String email = jwtUtil.extractEmail(token);
//
//        // Retrieve the User from the database using email
//        Users user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!"));
//
//        // Fetch booking history using user ID
//        List<BookingStatus> statuses = Arrays.asList(BookingStatus.COMPLETED, BookingStatus.CANCELLED, BookingStatus.BOOKED);
//        List<Bookings> bookings = bookingsDao.findByUserIdAndStatusIn(user.getId(), statuses);
//
//        if (bookings.isEmpty()) {
//            return new ArrayList<>();
//        }
//
//        return bookings.stream().map(booking -> new BookingResponseDTO(
//                booking.getBookingId(),
//                user.getId(),
//                booking.getRoom().getId(),
//                booking.getRoom().getName(),
//                user.getName(),
//                booking.getStartTime(),
//                booking.getEndTime(),
//                booking.getStatus()
//        )).collect(Collectors.toList());
//    }
//
//
//    // Retrieve booking history by room ID
//    public List<BookingResponseDTO> getRoomBookingHistory(Long roomId, List<BookingStatus> statuses) {
//        return bookingsDao.findByRoomIdAndStatusIn(roomId, statuses).stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
//    }
//
//    // Convert entity to DTO
//    private BookingResponseDTO convertToDTO(Bookings booking) {
//        return new BookingResponseDTO(
//                booking.getBookingId(),
//                booking.getUser().getId(),
//                booking.getRoom().getId(),
//                booking.getRoom().getName(),
//                booking.getUser().getName(),
//                booking.getStartTime(),
//                booking.getEndTime(),
//                booking.getStatus()
//        );
//    }
//    @Transactional
//    public UpdateBookingResponseDTO updateBooking(Long bookingId, UpdateBookingRequestDTO updateBookingRequestDTO) {
//        Bookings booking = bookingRepository.findById(bookingId)
//                .orElseThrow(() -> new ResourceNotFoundException("Booking not found!"));
//        if (!booking.getStatus().equals(BookingStatus.BOOKED)) {
//            throw new BadRequestException("Only 'BOOKED' status bookings can be updated!");
//        }
//
//        long overlappingBookings = bookingRepository.countOverlappingBookingsExcludingCurrent(
//                updateBookingRequestDTO.getRoomId(),
//                updateBookingRequestDTO.getStartTime(),
//                updateBookingRequestDTO.getEndTime(),
//                bookingId
//        );
//        if (overlappingBookings > 0) {
//            throw new ConflictException("The selected time slot conflicts with another booking!");
//        }
//        if (!booking.getRoom().getId().equals(updateBookingRequestDTO.getRoomId())) {
//            MeetingRooms newRoom = meetingRoomRepository.findById(updateBookingRequestDTO.getRoomId())
//                    .orElseThrow(() -> new ResourceNotFoundException("Room not found!"));
//            booking.setRoom(newRoom);
//        }
//        booking.setStartTime(updateBookingRequestDTO.getStartTime());
//        booking.setEndTime(updateBookingRequestDTO.getEndTime());
//        Bookings updatedBooking = bookingRepository.save(booking);
//
//        return new UpdateBookingResponseDTO(
//                updatedBooking.getBookingId(),
//                updatedBooking.getUser().getId(),
//                updatedBooking.getRoom().getId(),
//                updatedBooking.getRoom().getName(),
//                updatedBooking.getUser().getName(),
//                updatedBooking.getStartTime(),
//                updatedBooking.getEndTime(),
//                updatedBooking.getStatus()
//        );
//    }}
package com.meeting.room.service;

import com.meeting.room.enums.BookingStatus;
import com.meeting.room.dto.BookingRequestDTO;
import com.meeting.room.dto.BookingResponseDTO;
import com.meeting.room.dto.UpdateBookingRequestDTO;
import com.meeting.room.dto.UpdateBookingResponseDTO;

import java.util.List;

public interface BookingsService {
    BookingResponseDTO createBooking(BookingRequestDTO bookingRequestDTO, String token);
    void cancelBooking(Long bookingId, String token);
    boolean completeBooking(Long bookingId);
    List<BookingResponseDTO> getAllBookingHistory(List<BookingStatus> statuses);
    List<BookingResponseDTO> getUserBookingHistory(String token);
    List<BookingResponseDTO> getRoomBookingHistory(Long roomId, List<BookingStatus> statuses);
    UpdateBookingResponseDTO updateBooking(Long bookingId, UpdateBookingRequestDTO updateBookingRequestDTO);
}
