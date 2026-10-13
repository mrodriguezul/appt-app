package com.mrodriguezul.apptapp.web.mapper;

import com.mrodriguezul.apptapp.domain.model.Patient;
import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;
import com.mrodriguezul.apptapp.web.dto.PatientRequestDto;
import com.mrodriguezul.apptapp.web.dto.PatientResponseDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PatientWebMapperTest {
    private final PatientWebMapper mapper = Mappers.getMapper(PatientWebMapper.class);

    @Test
    void toModel_shouldMapRequestAndIgnoreId() {
        PatientRequestDto request = new PatientRequestDto(
                DocumentType.DNI, "12345678", "Ana", "Lopez",
                LocalDate.of(1990, 1, 15), "ana@example.com", "999999999", "MR-001");

        Patient result = mapper.toModel(request);

        assertThat(result.getId()).isNull();
        assertThat(result.getDocumentType()).isEqualTo(DocumentType.DNI);
        assertThat(result.getDocumentNumber()).isEqualTo("12345678");
        assertThat(result.getNames()).isEqualTo("Ana");
        assertThat(result.getSurnames()).isEqualTo("Lopez");
        assertThat(result.getDateOfBirth()).isEqualTo(LocalDate.of(1990, 1, 15));
        assertThat(result.getEmail()).isEqualTo("ana@example.com");
        assertThat(result.getPhoneNumber()).isEqualTo("999999999");
        assertThat(result.getMedicalRecordNumber()).isEqualTo("MR-001");
    }

    @Test
    void toResponse_shouldMapPatientIncludingInheritedFields() {
        Patient patient = Patient.builder()
                .id(7L)
                .documentType(DocumentType.DNI)
                .documentNumber("12345678")
                .names("Ana")
                .surnames("Lopez")
                .dateOfBirth(LocalDate.of(1990, 1, 15))
                .email("ana@example.com")
                .phoneNumber("999999999")
                .medicalRecordNumber("MR-001")
                .build();

        PatientResponseDto result = mapper.toResponse(patient);

        assertThat(result.id()).isEqualTo(7L);
        assertThat(result.documentType()).isEqualTo(DocumentType.DNI);
        assertThat(result.documentNumber()).isEqualTo("12345678");
        assertThat(result.names()).isEqualTo("Ana");
        assertThat(result.surnames()).isEqualTo("Lopez");
        assertThat(result.dateOfBirth()).isEqualTo(LocalDate.of(1990, 1, 15));
        assertThat(result.email()).isEqualTo("ana@example.com");
        assertThat(result.phoneNumber()).isEqualTo("999999999");
        assertThat(result.medicalRecordNumber()).isEqualTo("MR-001");
    }
}
