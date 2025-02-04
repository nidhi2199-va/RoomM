package com.MeetingRoom.RoomM.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    // Handle UserAlreadyExistsException
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Object> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        // Return a response with a custom error message
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);  // Error message shown in the popup
    }

    // Handle InvalidCredentialsException
//    @ExceptionHandler(InvalidCredentialsException.class)
//    public ResponseEntity<Object> handleInvalidCredentials(InvalidCredentialsException ex) {
//        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);  // Error message shown in the popup
//    }
//
//    // Generic exception handler for other errors
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<Object> handleGeneralException(Exception ex) {
//        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//    }
}
