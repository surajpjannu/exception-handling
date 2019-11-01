package com.jannu.springboot.exceptionhandling;

public class FirstNameShouldNotBeEmptyException extends RuntimeException {
    public FirstNameShouldNotBeEmptyException(String message) {
        super(message);
    }
}