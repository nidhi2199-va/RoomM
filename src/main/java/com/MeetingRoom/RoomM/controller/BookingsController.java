package com.MeetingRoom.RoomM.controller;

import com.MeetingRoom.RoomM.Enums.BookingStatus;
import com.MeetingRoom.RoomM.dto.*;
import com.MeetingRoom.RoomM.service.BookingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingsController {

    private final BookingsService bookingService;

    // Create a new booking
    @PostMapping("/create")
    public ResponseEntity<?> createBooking(@RequestBody @Valid BookingRequestDTO bookingRequestDTO) {
        try {
            BookingResponseDTO bookingResponseDTO = bookingService.createBooking(bookingRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(bookingResponseDTO);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", e.getReason()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred!"));
        }
    }


    @PutMapping("/cancel")
    public ResponseEntity<CancelBookingResponseDTO> cancelBooking(@RequestBody CancelBookingRequestDTO requestDTO) {
        try {
            CancelBookingResponseDTO response = bookingService.cancelBooking(requestDTO);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new CancelBookingResponseDTO(null, e.getMessage()));
        }
    }
    // Endpoint to manually trigger the status update
    @PutMapping("/{bookingId}/complete")
    public ResponseEntity<String> completeBooking(@PathVariable Long bookingId) {
        boolean updated = bookingService.completeBooking(bookingId);

        if (updated) {
            return ResponseEntity.ok("Booking marked as COMPLETED successfully.");
        } else {
            return ResponseEntity.badRequest().body("Booking not found or already completed.");
        }
    }
    @GetMapping("/history")
    public ResponseEntity<List<BookingResponseDTO>> getAllBookingHistory() {
        List<BookingStatus> statuses = Arrays.asList(BookingStatus.COMPLETED, BookingStatus.CANCELLED);
        return ResponseEntity.ok(bookingService.getAllBookingHistory(statuses));
    }

    @GetMapping("/history/user/{userId}")
    public ResponseEntity<List<BookingResponseDTO>> getUserBookingHistory(@PathVariable Long userId) {
        List<BookingResponseDTO> bookingHistory = bookingService.getUserBookingHistory(userId);
        return ResponseEntity.ok(bookingHistory);
    }

    // Get booking history for a specific room
    @GetMapping("/history/room/{roomId}")
    public ResponseEntity<List<BookingResponseDTO>> getRoomBookingHistory(@PathVariable Long roomId) {
        List<BookingStatus> statuses = Arrays.asList(BookingStatus.COMPLETED, BookingStatus.CANCELLED);
        return ResponseEntity.ok(bookingService.getRoomBookingHistory(roomId, statuses));
    }
    @PutMapping("/{bookingId}")
    public ResponseEntity<UpdateBookingResponseDTO> updateBooking(@PathVariable Long bookingId, @RequestBody UpdateBookingRequestDTO bookingRequestDTO) {
        UpdateBookingResponseDTO updatedBooking = bookingService.updateBooking(bookingId, bookingRequestDTO);
        return ResponseEntity.ok(updatedBooking);
    }
    }
