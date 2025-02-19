//package com.MeetingRoom.RoomM.service;
//
//import com.MeetingRoom.RoomM.Enums.Role;
//import com.MeetingRoom.RoomM.Exceptions.InvalidCredentialsException;
//import com.MeetingRoom.RoomM.Exceptions.UserAlreadyExistsException;
//import com.MeetingRoom.RoomM.Exceptions.UserNotFoundException;
//import com.MeetingRoom.RoomM.dto.LoginRequestDTO;
//import com.MeetingRoom.RoomM.dto.SignupRequestDTO;
//import com.MeetingRoom.RoomM.model.Users;
//import com.MeetingRoom.RoomM.dao.UserDao;
//import com.MeetingRoom.RoomM.Utils.JwtUtil;
//import com.MeetingRoom.RoomM.dto.AuthResponseDTO;
//import lombok.extern.slf4j.Slf4j;
//import org.mindrot.jbcrypt.BCrypt;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//
//@Slf4j
//@Service
//public class AuthService {
//
//    private final UserDao userDao;
//    private final JwtUtil jwtUtil;
//    private static final String ADMIN_SECRET = "2199";
//    public AuthService(JwtUtil jwtUtil, UserDao userDao) {
//        this.jwtUtil = jwtUtil;
//        this.userDao = userDao;
//    }
//
//    public String signup(SignupRequestDTO signupRequestDTO) {
//
//        Optional<Users> existingUser = userDao.findByEmail(signupRequestDTO.getEmail());
//        if (!signupRequestDTO.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
//            throw new InvalidCredentialsException("Invalid email format. Example: user@example.com");
//        }
//        if (!signupRequestDTO.getPhoneNumber().matches("^\\d{10}$")) {
//            throw new InvalidCredentialsException("Invalid phone number format. Must be 10 digits.");
//        }
//        if (existingUser.isPresent()) {
//            throw new UserAlreadyExistsException("Email already exists");
//        }
//        Users user = new Users();
//        String hashedPassword = BCrypt.hashpw(signupRequestDTO.getPassword(), BCrypt.gensalt());
//        user.setPassword(hashedPassword);
//        user.setEmail(signupRequestDTO.getEmail());
//        user.setPhone(signupRequestDTO.getPhoneNumber());
//        user.setName(signupRequestDTO.getName());
//        user.setDepartment(signupRequestDTO.getDepartment());
//        if ("2199".equals(signupRequestDTO.getSecretCode())) {
//            user.setRole(Role.ADMIN);
//        } else {
//            user.setRole(Role.USER);
//        }
//        userDao.save(user);
//        return "User is Registered Succesfully!!"; // Or return a custom response without the token
//    }
//
//    public AuthResponseDTO login(LoginRequestDTO loginRequestDTO) {
//        log.info("Login Request");
//        Optional<Users> existingUser = userDao.findByEmail(loginRequestDTO.getEmail());
//        if (existingUser.isEmpty()) {
//            throw new UserNotFoundException("User not found!!");
//        }
//        Users user = existingUser.get();
//        if (!BCrypt.checkpw(loginRequestDTO.getPassword(), user.getPassword())) {
//            throw new InvalidCredentialsException("Invalid password");
//        }
//        String token = jwtUtil.generateToken(user.getEmail());
//        return new AuthResponseDTO(token);
//    }
//
//}
package com.MeetingRoom.RoomM.service;

import com.MeetingRoom.RoomM.dto.AuthResponseDTO;
import com.MeetingRoom.RoomM.dto.LoginRequestDTO;
import com.MeetingRoom.RoomM.dto.SignupRequestDTO;

public interface AuthService {
    String signup(SignupRequestDTO signupRequestDTO);
    AuthResponseDTO login(LoginRequestDTO loginRequestDTO);
}
