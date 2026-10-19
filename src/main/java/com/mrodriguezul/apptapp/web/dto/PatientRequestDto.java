package com.mrodriguezul.apptapp.web.dto;

import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Patient data used to create or update a patient")
public record PatientRequestDto(
        @Schema(description = "Document type", example = "DNI", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        DocumentType documentType,

        @Schema(description = "Document number", example = "12345678", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String documentNumber,

        @Schema(description = "Given names", example = "Ana María", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String names,

        @Schema(description = "Family names", example = "Pérez Gómez", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String surnames,

        @Schema(description = "Date of birth; must be in the past", example = "1990-05-20", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        @Past
        LocalDate dateOfBirth,

        @Schema(description = "Email address", example = "ana.perez@example.com")
        @Email
        String email,

        @Schema(description = "Phone number", example = "+51987654321")
        String phoneNumber,

        @Schema(description = "Medical record number", example = "MR-000123", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String medicalRecordNumber
) {}
