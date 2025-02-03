package com.MeetingRoom.RoomM.dto;

import com.MeetingRoom.RoomM.Enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class CancelBookingResponseDTO {
    private Long bookingId;
    private String message;
}
