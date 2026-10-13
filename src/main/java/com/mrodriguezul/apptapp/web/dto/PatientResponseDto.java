package com.mrodriguezul.apptapp.web.dto;

import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;

import java.time.LocalDate;

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
