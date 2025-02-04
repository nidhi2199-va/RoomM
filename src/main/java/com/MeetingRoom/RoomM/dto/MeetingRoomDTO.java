package com.MeetingRoom.RoomM.dto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
public class MeetingRoomDTO {
    private Long id;
    private String name;
    private int capacity;
    private boolean isAvailable;

}
