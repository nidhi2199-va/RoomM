package com.meeting.room.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CancelBookingResponseDTO {
    private Long bookingId;
    private String message;
}
