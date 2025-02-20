package com.meeting.room;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  // Enables Spring Scheduler
public class RoomApplication {
	public static void main(String[] args) {
		SpringApplication.run(RoomApplication.class, args);
	}
}