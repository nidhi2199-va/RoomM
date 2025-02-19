package com.MeetingRoom.RoomM.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;


import java.util.List;

@Entity
@Table(name = "meeting_rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingRooms {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Meeting Room ID
    @Column(unique = true)
    private String name;  // Name of the meeting room

    private int capacity;  // Room capacity

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Bookings> bookings;
    @Column(name = "deleted", nullable = false)
    private boolean deleted = false; // Soft delete flag

//   @OneToMany(mappedBy = "user")
//   private List<Users> user;  // List of users who have access to this room
}