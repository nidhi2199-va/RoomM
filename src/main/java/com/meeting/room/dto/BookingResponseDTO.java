package com.meeting.room.dto;

import com.meeting.room.enums.BookingStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponseDTO {

    private Long id;             // Booking ID (bid)
    private Long userId;         // User ID (uid)
    private Long roomId;         // Room ID
    private String roomName;     // Room Name
    private String userName;     // User Name
    private LocalDateTime startTime;  // Start Time
    private LocalDateTime endTime;    // End Time
    private BookingStatus status;           // Booking Status
}
