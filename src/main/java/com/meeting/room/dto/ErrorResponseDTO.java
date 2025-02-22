package com.meeting.room.dto;

public class ErrorResponseDTO {
    public String message;
    public String status;
    public ErrorResponseDTO(String message, String status) {
        this.message = message;
        this.status = status;
    }
}
