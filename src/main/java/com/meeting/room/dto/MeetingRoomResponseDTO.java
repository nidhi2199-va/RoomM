package com.meeting.room.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeetingRoomResponseDTO {
    private Long id;
    private String name;
    private int capacity;
    private String status;
}
