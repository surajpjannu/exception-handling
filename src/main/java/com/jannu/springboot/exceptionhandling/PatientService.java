package com.jannu.springboot.exceptionhandling;

import org.springframework.stereotype.Service;

@Service
public class PatientService {

    public Patient createPatient(Patient patient) {
        if (patient.getFirstName() == null || patient.getFirstName().isEmpty()) {
            throw new FirstNameShouldNotBeEmptyException("required first name");
        }
        return patient;
    }
}