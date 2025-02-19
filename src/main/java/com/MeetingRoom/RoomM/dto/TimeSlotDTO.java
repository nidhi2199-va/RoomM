package com.MeetingRoom.RoomM.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Data
public class TimeSlotDTO {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
