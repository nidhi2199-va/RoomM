package com.MeetingRoom.RoomM.model;
import com.MeetingRoom.RoomM.Enums.Department;
import com.MeetingRoom.RoomM.Enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // User ID

    private String name; // User Name

    private String email; // User Email

    private String phone;
    private String password; // Hashed User Password

    @Enumerated(EnumType.STRING)
    private Department department; // Department of the user (HR, TECH, SALES, etc.)
    @Enumerated(EnumType.STRING)
    private Role role; // Role to differentiate between "User" or "Admin"


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookings> bookings;
}