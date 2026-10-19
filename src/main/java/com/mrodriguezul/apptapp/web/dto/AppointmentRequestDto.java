package com.mrodriguezul.apptapp.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Appointment data used to create or update an appointment")
public record AppointmentRequestDto(
        @Schema(description = "Doctor identifier", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull Long doctorId,
        @Schema(description = "Patient identifier", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull Long patientId,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        @Schema(description = "Appointment date and time, in the future, with minute precision", example = "2030-05-20T10:30", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull @Future LocalDateTime appointmentDate,
        @Schema(description = "Reason for the appointment", example = "Annual medical check-up", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String reason) {
}
