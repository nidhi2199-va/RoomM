package com.MeetingRoom.RoomM.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateBookingRequestDTO {

    private Long roomId;  // The new room ID to update
    private LocalDateTime startTime;  // The updated start time
    private LocalDateTime endTime;  // The updated end time
}
