package com.MeetingRoom.RoomM.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class AddRoomRequestDTO {

   // private Long id;
    private String name;
    private Integer capacity;
   // private List<String> equipmentList;
    private Boolean isAvailable;
}
