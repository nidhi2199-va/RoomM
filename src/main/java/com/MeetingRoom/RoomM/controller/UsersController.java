package com.MeetingRoom.RoomM.controller;

import com.MeetingRoom.RoomM.dto.AuthResponseDTO;
import com.MeetingRoom.RoomM.dto.LoginRequestDTO;
import com.MeetingRoom.RoomM.dto.SignupRequestDTO;
import com.MeetingRoom.RoomM.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UsersController {

    private final AuthService authService;

    // SignUp API (Using DTO - SignupRequest)
    @PostMapping("/signup")
    public AuthResponseDTO signUp(@RequestBody SignupRequestDTO signupRequest) {
        return authService.signup(signupRequest);
    }

    @PostMapping("/login")
    public AuthResponseDTO logIn(@RequestBody LoginRequestDTO loginRequest) {
        return authService.login(loginRequest);
    }
}
