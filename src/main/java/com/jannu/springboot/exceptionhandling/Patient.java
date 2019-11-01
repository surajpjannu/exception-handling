package com.jannu.springboot.exceptionhandling;

import lombok.Data;

import java.util.Date;

@Data
public class Patient {
    private String firstName;
    private String middleName;
    private String lastName;
    private int age;
    private Date dob;
}