package com.meeting.room.model;


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
public class MeetingRooms extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Meeting Room ID

    @Column(unique = true)
    private String name;  // Name of the meeting room

    private int capacity;  // Room capacity

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Bookings> bookings;

}