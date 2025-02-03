package com.MeetingRoom.RoomM.service;

import com.MeetingRoom.RoomM.dto.LoginRequestDTO;
import com.MeetingRoom.RoomM.dto.SignupRequestDTO;
import com.MeetingRoom.RoomM.model.Users;
import com.MeetingRoom.RoomM.dao.UserDao;
import com.MeetingRoom.RoomM.Utils.JwtUtil;
import com.MeetingRoom.RoomM.dto.AuthResponseDTO;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserDao userDao;
    private final JwtUtil jwtUtil;

    public AuthService(JwtUtil jwtUtil, UserDao userDao) {
        this.jwtUtil = jwtUtil;
        this.userDao = userDao;
    }

    // SignUp method for saving the user
    public AuthResponseDTO signup(SignupRequestDTO signupRequestDTO) {
        // Check if the user already exists with the given email
        Optional<Users> existingUser = userDao.findByEmail(signupRequestDTO.getEmail());

        if (existingUser.isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // Create new user and set values
        Users user = new Users();
        String hashedPassword = BCrypt.hashpw(signupRequestDTO.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPassword);
        user.setEmail(signupRequestDTO.getEmail());
        //user.setEmpId(signupRequestDTO.getEmpId());
        user.setPhone(signupRequestDTO.getPhoneNumber());

        user.setName(signupRequestDTO.getName());
        user.setRole(signupRequestDTO.getRole());
        user.setDepartment(signupRequestDTO.getDepartment());

        // Save the user to the database
        userDao.save(user);

        // Generate JWT token after successful registration
        String token = jwtUtil.generateToken(signupRequestDTO.getEmail());

        // Return the response with the token
        return new AuthResponseDTO(token);
    }

    public AuthResponseDTO login(LoginRequestDTO loginRequestDTO) {
        // Find the user by email
        Optional<Users> existingUser = userDao.findByEmail(loginRequestDTO.getEmail());

        if (existingUser.isEmpty()) {
            throw new RuntimeException("Invalid email or password");
        }

        Users user = existingUser.get();

        // Check if the provided password matches the stored hashed password
        if (!BCrypt.checkpw(loginRequestDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Generate JWT token after successful login
        String token = jwtUtil.generateToken(user.getEmail());

        // Return the response with the token
        return new AuthResponseDTO(token);
    }

}