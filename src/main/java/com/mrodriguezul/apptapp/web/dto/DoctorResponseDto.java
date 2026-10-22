package com.mrodriguezul.apptapp.web.dto;

import com.mrodriguezul.apptapp.domain.model.Speciality;
import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Doctor returned by the API")
public record DoctorResponseDto(
        Long id,
        DocumentType documentType,
        String documentNumber,
        String names,
        String surnames,
        LocalDate dateOfBirth,
        String email,
        String phoneNumber,
        SpecialityResponseDto speciality
) {}
