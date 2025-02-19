package com.MeetingRoom.RoomM.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class RoomWithTimeSlotsDTO {
    private Long roomId;
    private String name;
    private int capacity;
    private List<TimeSlotDTO> timeSlots;
}

