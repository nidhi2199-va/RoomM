package com.MeetingRoom.RoomM.controller;

import com.MeetingRoom.RoomM.dto.AuthResponseDTO;
import com.MeetingRoom.RoomM.dto.LoginRequestDTO;
import com.MeetingRoom.RoomM.dto.SignupRequestDTO;
import com.MeetingRoom.RoomM.service.AuthService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
public class UsersController {

    private final AuthService authService;

    @Autowired
    public UsersController(AuthService authService) {
        this.authService = authService;
    }
    // SignUp API (Using DTO - SignupRequest)
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequestDTO signupRequestDTO) {
        // Call the signup method from AuthService
        String message = authService.signup(signupRequestDTO);
        // Return success message
        return ResponseEntity.ok(message);  // Return success message with HTTP 200 OK
    }
    // Login API (Using DTO - LoginRequest)
    @PostMapping("/login")
    public AuthResponseDTO logIn(@RequestBody LoginRequestDTO loginRequest) {
        // Call the login method from AuthService to generate JWT Token
        //log.info("Imin the login controller");
        return authService.login(loginRequest);
    }
}
