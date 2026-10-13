package com.mrodriguezul.apptapp.persistence.mapper;

import com.mrodriguezul.apptapp.domain.model.Patient;
import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;
import com.mrodriguezul.apptapp.persistence.entity.PatientEntity;
import com.mrodriguezul.apptapp.persistence.entity.PersonEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PatientPersistenceMapperTest {
    private final PatientPersistenceMapper mapper = Mappers.getMapper(PatientPersistenceMapper.class);

    @Test
    void toModel_shouldMapPatientAndInheritedPersonFields() {
        PersonEntity person = person(7L);
        PatientEntity entity = new PatientEntity();
        entity.setId(7L);
        entity.setPersonEntity(person);
        entity.setMedicalRecordNumber("MR-001");

        Patient result = mapper.toModel(entity);

        assertThat(result.getId()).isEqualTo(7L);
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
    void toEntity_shouldMapPatientAndPutIdInPersonEntity() {
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

        PatientEntity result = mapper.toEntity(patient);

        assertThat(result.getId()).isEqualTo(7L);
        assertThat(result.getPersonEntity()).isNotNull();
        assertThat(result.getPersonEntity().getId()).isEqualTo(7L);
        assertThat(result.getPersonEntity().getDocumentType()).isEqualTo(DocumentType.DNI);
        assertThat(result.getPersonEntity().getDocumentNumber()).isEqualTo("12345678");
        assertThat(result.getPersonEntity().getNames()).isEqualTo("Ana");
        assertThat(result.getPersonEntity().getSurnames()).isEqualTo("Lopez");
        assertThat(result.getPersonEntity().getDateOfBirth()).isEqualTo(LocalDate.of(1990, 1, 15));
        assertThat(result.getPersonEntity().getEmail()).isEqualTo("ana@example.com");
        assertThat(result.getPersonEntity().getPhoneNumber()).isEqualTo("999999999");
        assertThat(result.getMedicalRecordNumber()).isEqualTo("MR-001");
        assertThat(result.getAppointments()).isNull();
    }

    private PersonEntity person(Long id) {
        PersonEntity person = new PersonEntity();
        person.setId(id);
        person.setDocumentType(DocumentType.DNI);
        person.setDocumentNumber("12345678");
        person.setNames("Ana");
        person.setSurnames("Lopez");
        person.setDateOfBirth(LocalDate.of(1990, 1, 15));
        person.setEmail("ana@example.com");
        person.setPhoneNumber("999999999");
        return person;
    }
}
