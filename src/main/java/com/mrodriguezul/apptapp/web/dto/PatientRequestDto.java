package com.mrodriguezul.apptapp.web.dto;

import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record PatientRequestDto(
        @NotNull
        DocumentType documentType,

        @NotBlank
        String documentNumber,

        @NotBlank
        String names,

        @NotBlank
        String surnames,

        @NotNull
        @Past
        LocalDate dateOfBirth,

        @Email
        String email,

        String phoneNumber,

        @NotBlank
        String medicalRecordNumber
) {}
