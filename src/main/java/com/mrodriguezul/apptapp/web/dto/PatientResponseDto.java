package com.mrodriguezul.apptapp.web.dto;

import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;

import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Patient returned by the API")
public record PatientResponseDto(
        Long id,
        DocumentType documentType,
        String documentNumber,
        String names,
        String surnames,
        LocalDate dateOfBirth,
        String email,
        String phoneNumber,
        String medicalRecordNumber
) {}
