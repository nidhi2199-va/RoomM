package com.MeetingRoom.RoomM.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeetingRoomsResponseDTO {
    private Long id;
    private String name;
    private int capacity;
}
