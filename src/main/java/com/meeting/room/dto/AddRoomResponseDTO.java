package com.meeting.room.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AddRoomResponseDTO {
   // private Long id;
    private String name;
    private Integer capacity;
    private Boolean isAvailable;
   // private List<String> equipmentList;
}
