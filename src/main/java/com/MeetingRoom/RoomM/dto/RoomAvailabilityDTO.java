package com.MeetingRoom.RoomM.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class RoomAvailabilityDTO {
    private List<Long> availableRoomIds;  // List of available room IDs
}
