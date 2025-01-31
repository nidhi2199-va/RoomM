package com.MeetingRoom.RoomM.service;

import com.MeetingRoom.RoomM.dto.AuthResponseDTO;
import com.MeetingRoom.RoomM.dao.UserDao;
import com.MeetingRoom.RoomM.dto.SignupRequestDTO;
import com.MeetingRoom.RoomM.model.Users;
import com.MeetingRoom.RoomM.Utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private JwtUtil jwtUtil;

    // SignUp method for saving the user, using SignupRequestDTO
//    public AuthResponseDTO signUp(SignupRequestDTO signupRequestDTO) {
//        // Check if user already exists
//        Optional<Users> existingUser = userDao.findByEmail(signupRequestDTO.getEmail());
//
//        if (existingUser.isPresent()) {
//            throw new RuntimeException("Email already exists");
//        }
//
//        // Convert SignupRequestDTO to Users entity
//        Users user = new Users();
//        String hashedPassword = BCrypt.hashpw(signupRequestDTO.getPassword(), BCrypt.gensalt());
//        user.setPassword(hashedPassword);
//        user.setEmail(signupRequestDTO.getEmail());
//        //user.setEmpId(signupRequestDTO.getEmpId());
//        user.setPhone(signupRequestDTO.getPhoneNumber());
//
//        user.setName(signupRequestDTO.getName());
//        user.setRole(signupRequestDTO.getRole());
//        user.setDepartment(signupRequestDTO.getDepartment());
//        //user.setRole(signupRequestDTO.getRole());
//
//        // Save the user to the database
//        userDao.save(user);
//
//        // Generate JWT token
//        String token = jwtUtil.generateToken(signupRequestDTO.getEmail());
//
//        return new AuthResponseDTO(token);
//    }


}
