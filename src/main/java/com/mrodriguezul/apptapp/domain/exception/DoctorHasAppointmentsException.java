package com.mrodriguezul.apptapp.domain.exception;

public class DoctorHasAppointmentsException extends RuntimeException {

    public DoctorHasAppointmentsException(String message, Throwable cause) {
        super(message, cause);
    }
}
