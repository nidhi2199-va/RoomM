package com.meeting.room.controller;

import com.meeting.room.enums.BookingStatus;
import com.meeting.room.dto.BookingRequestDTO;
import com.meeting.room.dto.BookingResponseDTO;
import com.meeting.room.dto.UpdateBookingRequestDTO;
import com.meeting.room.dto.UpdateBookingResponseDTO;
import com.meeting.room.service.BookingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    public ResponseEntity<?> createBooking(
            @RequestBody @Valid BookingRequestDTO bookingRequestDTO,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            // Extract the token without "Bearer " prefix
            String jwtToken = token.replace("Bearer ", "");

            // Pass the token to the service layer
            BookingResponseDTO bookingResponseDTO = bookingService.createBooking(bookingRequestDTO, jwtToken);
            return ResponseEntity.status(HttpStatus.CREATED).body(bookingResponseDTO);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", e.getReason()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred!"));
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
        List<BookingStatus> statuses = Arrays.asList(BookingStatus.COMPLETED, BookingStatus.BOOKED);
        return ResponseEntity.ok(bookingService.getAllBookingHistory(statuses));
    }

    @GetMapping("/history/user")
    public ResponseEntity<?> getUserBookingHistory(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            // Remove "Bearer " from the token
            String jwtToken = token.replace("Bearer ", "");

            // Fetch booking history using the token

            List<BookingResponseDTO> bookingHistory = bookingService.getUserBookingHistory(jwtToken);

            return ResponseEntity.ok(bookingHistory);
        } catch (ResponseStatusException e) {
            // Return proper HTTP status with error message
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", e.getReason()));
        } catch (Exception e) {
            // Log and return internal server error

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred"));
        }
    }


    // Get booking history for a specific room
    @GetMapping("/history/room/{roomId}")
    public ResponseEntity<List<BookingResponseDTO>> getRoomBookingHistory(@PathVariable Long roomId) {
        List<BookingStatus> statuses = Arrays.asList(BookingStatus.COMPLETED, BookingStatus.CANCELLED);
        return ResponseEntity.ok(bookingService.getRoomBookingHistory(roomId, statuses));
    }
    @PutMapping("/cancel")
    public ResponseEntity<?> cancelBooking(
            @RequestBody Map<String, Long> requestBody,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            // Extract booking ID from request body
            Long bookingId = requestBody.get("bookingId");
            if (bookingId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Booking ID is required"));
            }
            String jwtToken = token.replace("Bearer ", "");
            bookingService.cancelBooking(bookingId, jwtToken);

            return ResponseEntity.ok(Map.of("message", "Booking canceled successfully!"));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", e.getReason()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred!"));
        }
    }
    @PutMapping("/{bookingId}")
    public ResponseEntity<UpdateBookingResponseDTO> updateBooking(@PathVariable Long bookingId, @RequestBody UpdateBookingRequestDTO bookingRequestDTO) {
        UpdateBookingResponseDTO updatedBooking = bookingService.updateBooking(bookingId, bookingRequestDTO);
        return ResponseEntity.ok(updatedBooking);
    }
}
