package com.mrodriguezul.apptapp.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Appointment returned by the API")
public record AppointmentResponseDto(
        Long id,
        Long doctorId,
        Long patientId,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime appointmentDate,
        String reason) {
}
