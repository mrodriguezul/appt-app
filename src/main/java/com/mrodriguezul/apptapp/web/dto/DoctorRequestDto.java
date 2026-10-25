package com.mrodriguezul.apptapp.web.dto;

import com.mrodriguezul.apptapp.domain.model.Speciality;
import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Schema(description = "Doctor data used to create or update a doctor")
public record DoctorRequestDto(
        @Schema(description = "Document type", example = "DNI", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        DocumentType documentType,

        @Schema(description = "Document number", example = "12345678", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String documentNumber,

        @Schema(description = "Given names", example = "Juan Carlos", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String names,

        @Schema(description = "Family names", example = "García López", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String surnames,

        @Schema(description = "Date of birth; must be in the past", example = "1985-05-20", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        @Past
        LocalDate dateOfBirth,

        @Schema(description = "Email address", example = "juan.garcia@example.com")
        @Email
        String email,

        @Schema(description = "Phone number", example = "+51987654321")
        String phoneNumber,

        @Schema(description = "ID of an existing medical specialty", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        @Positive
        Long specialityId
) {}
