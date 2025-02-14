package com.MeetingRoom.RoomM.dto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeleteMeetingRoomRequestDTO {

        private Long roomId;
}

