package com.MeetingRoom.RoomM.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CancelBookingRequestDTO {
    private Long bookingId;

}
