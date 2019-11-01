package com.jannu.springboot.exceptionhandling;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@RestControllerAdvice
public class ApplicationExceptionHandler {
    
    @ExceptionHandler(FirstNameShouldNotBeEmptyException.class)
    public ResponseEntity<CustomExceptionResponse> handleFirstNameShouldNotBeEmptyException(
            FirstNameShouldNotBeEmptyException ex, WebRequest web){
        final CustomExceptionResponse response = new CustomExceptionResponse();
        response.setMessage(ex.getMessage());
        response.setTimeStamp(new Date().toString());
        return new ResponseEntity<>(response, HttpStatus.PRECONDITION_REQUIRED);
    }    
}