package com.meeting.room.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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

