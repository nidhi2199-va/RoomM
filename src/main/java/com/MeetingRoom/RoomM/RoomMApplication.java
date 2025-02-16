package com.MeetingRoom.RoomM;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  // Enables Spring Scheduler
public class RoomMApplication {
	public static void main(String[] args) {
		SpringApplication.run(RoomMApplication.class, args);
	}
}