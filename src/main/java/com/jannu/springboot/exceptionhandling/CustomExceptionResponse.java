package com.jannu.springboot.exceptionhandling;

import lombok.Data;

@Data
public class CustomExceptionResponse {
    private String message;
    private String timeStamp;
}