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
    @CrossOrigin(origins = "http://localhost:5174")  // Allow cross-origin requests for this specific endpoint
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequestDTO signupRequestDTO) {
        // Call the signup method from AuthService
        String message = authService.signup(signupRequestDTO);

        // Return success message
        return ResponseEntity.ok(message);  // Return success message with HTTP 200 OK
    }

    // Login API (Using DTO - LoginRequest)
    @CrossOrigin(origins = "http://localhost:5174")  // Allow cross-origin requests for this specific endpoint
    @PostMapping("/login")
    public AuthResponseDTO logIn(@RequestBody LoginRequestDTO loginRequest) {
        // Call the login method from AuthService to generate JWT Token
        return authService.login(loginRequest);
    }
}
