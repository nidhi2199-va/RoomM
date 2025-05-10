package com.meeting.room.dao;

import com.meeting.room.model.Users;
import java.util.List;
import java.util.Optional;

public interface UserDao {

    Users save(Users user);
    Optional<Users> findById(Long userId);
    List<Users> findAll();
    void deleteById(Long userId);
    Optional<Users> findByEmail(String email);
}
