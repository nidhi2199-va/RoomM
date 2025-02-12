package com.MeetingRoom.RoomM.dto;

import com.MeetingRoom.RoomM.Enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingHistoryResponseDTO {
    private Long id;
    private String roomName;
    private int capacity;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BookingStatus status;
}
