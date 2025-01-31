package com.MeetingRoom.RoomM.model;


import jakarta.persistence.*;
import lombok.*;


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
    private Long roomId;  // Meeting Room ID

    private String name;  // Name of the meeting room

    private int capacity;  // Room capacity

    @ElementCollection
    private List<String> equipment;  // List of equipment available in the room (Projector, Whiteboard, etc.)

    @OneToMany(mappedBy = "room")
    private List<Bookings> bookings;  // List of bookings for this room

//    @OneToMany(mappedBy = "")
//    private List<Users> users;  // List of users who have access to this room
}