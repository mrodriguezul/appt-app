package com.mrodriguezul.apptapp.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record AppointmentResponseDto(
        Long id,
        Long doctorId,
        Long patientId,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime appointmentDate,
        String reason) {
}
