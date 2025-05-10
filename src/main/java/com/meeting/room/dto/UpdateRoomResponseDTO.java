package com.meeting.room.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateRoomResponseDTO {
    private Long id;
    private String name;
    private int capacity;

}
