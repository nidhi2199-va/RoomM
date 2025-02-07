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
//@CrossOrigin(origins = "http://localhost:3001")  // Allow only frontend origin
public class UsersController {

    private final AuthService authService;

    // SignUp API (Using DTO - SignupRequest)
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequestDTO signupRequestDTO) {
        // Call the signup method from AuthService
        String message = authService.signup(signupRequestDTO);

        // Return success message
        return ResponseEntity.ok(message);  // Return success message with HTTP 200 OK
    }

    @PostMapping("/login")
    public AuthResponseDTO logIn(@RequestBody LoginRequestDTO loginRequest) {
        return authService.login(loginRequest);
    }
}
