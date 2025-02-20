package com.meeting.room.model;
import com.meeting.room.enums.BookingStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Bookings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private MeetingRooms room;

    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalDateTime startTime;

    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

}
