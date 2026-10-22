package com.mrodriguezul.apptapp.domain.exception;

public class DoctorAlreadyExistsException extends RuntimeException {

    public DoctorAlreadyExistsException(String message) {
        super(message);
    }
}
