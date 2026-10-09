package com.mrodriguezul.apptapp.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AppointmentRequestDto(
        @NotNull Long doctorId,
        @NotNull Long patientId,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        @NotNull @Future LocalDateTime appointmentDate,
        @NotBlank String reason) {
}
