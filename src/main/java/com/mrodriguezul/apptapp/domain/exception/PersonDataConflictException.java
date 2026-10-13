package com.mrodriguezul.apptapp.domain.exception;

public class PersonDataConflictException extends RuntimeException {

    public PersonDataConflictException(String message) {
        super(message);
    }
}
