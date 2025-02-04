package com.MeetingRoom.RoomM.controller;

import com.MeetingRoom.RoomM.dto.*;
import com.MeetingRoom.RoomM.service.BookingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingsController {

    private final BookingsService bookingService;

    // Create a new booking
    @PostMapping("/create")
    public ResponseEntity<BookingResponseDTO> createBooking(@RequestBody @Valid BookingRequestDTO bookingRequestDTO) {
        try {
            // Create the booking and return response
            BookingResponseDTO bookingResponseDTO = bookingService.createBooking(bookingRequestDTO);
            return new ResponseEntity<>(bookingResponseDTO, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // Handle conflict or error
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);  // Conflict if the time slot is already booked
        }
    }

    @PutMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public UpdateBookingResponseDTO updateBooking(@PathVariable Long bookingId, @RequestBody UpdateBookingRequestDTO updateBookingRequestDTO) {
        return bookingService.updateBooking(bookingId, updateBookingRequestDTO);
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

}