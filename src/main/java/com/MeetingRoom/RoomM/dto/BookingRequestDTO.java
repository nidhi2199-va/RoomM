package com.MeetingRoom.RoomM.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequestDTO {
    private Long roomId;
    private Long userId;       // User ID
    private LocalDateTime startTime;   // Start time
    private LocalDateTime endTime;     // End time
}
