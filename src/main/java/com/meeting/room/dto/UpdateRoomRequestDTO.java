package com.meeting.room.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRoomRequestDTO {
    private String name;
    private int capacity;

}
