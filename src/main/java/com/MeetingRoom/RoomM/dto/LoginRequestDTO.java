package com.MeetingRoom.RoomM.dto;

import com.MeetingRoom.RoomM.Enums.Department;
import com.MeetingRoom.RoomM.Enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginRequestDTO {

    private  String email;

    private  String password;



}
