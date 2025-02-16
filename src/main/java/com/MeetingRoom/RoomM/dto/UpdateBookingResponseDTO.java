package com.MeetingRoom.RoomM.dto;
import com.MeetingRoom.RoomM.Enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@NoArgsConstructor  // ✅ Default Constructor (Needed for Deserialization)
@AllArgsConstructor // ✅ Constructor with All Fields (Fixes your error)
@Data
public class UpdateBookingResponseDTO {

    private Long bookingId;
    private Long userId;
    private Long roomId;
    private String roomName;
    private String userName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BookingStatus status;
}
