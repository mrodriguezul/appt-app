package com.mrodriguezul.apptapp.persistence.adapter;

import com.mrodriguezul.apptapp.domain.exception.PatientAlreadyExistsException;
import com.mrodriguezul.apptapp.domain.exception.PersonDataConflictException;
import com.mrodriguezul.apptapp.domain.exception.ResourceNotFoundException;
import com.mrodriguezul.apptapp.domain.model.Patient;
import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;
import com.mrodriguezul.apptapp.persistence.crud.PatientCrudRepository;
import com.mrodriguezul.apptapp.persistence.crud.PersonCrudRepository;
import com.mrodriguezul.apptapp.persistence.entity.PatientEntity;
import com.mrodriguezul.apptapp.persistence.entity.PersonEntity;
import com.mrodriguezul.apptapp.persistence.mapper.PatientPersistenceMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientRepositoryTest {
    @Mock
    private PatientCrudRepository patientCrudRepository;
    @Mock
    private PersonCrudRepository personCrudRepository;
    @Mock
    private PatientPersistenceMapper patientPersistenceMapper;

    private PatientRepository patientRepository;

    @BeforeEach
    void setUp() {
        patientRepository = new PatientRepository(
                patientCrudRepository, personCrudRepository, patientPersistenceMapper);
    }

    @Test
    void create_whenPersonDoesNotExist_shouldSavePersonAndPatient() {
        Patient patient = patient("MR-001");
        PersonEntity mappedPerson = person(patient);
        PatientEntity mappedPatient = patientEntity(mappedPerson, "MR-001");
        PatientEntity savedPatient = patientEntity(mappedPerson, "MR-001");
        when(patientPersistenceMapper.toEntity(patient)).thenReturn(mappedPatient);
        when(personCrudRepository.findByDocumentTypeAndDocumentNumber(DocumentType.DNI, "12345678"))
                .thenReturn(Optional.empty());
        when(personCrudRepository.save(mappedPerson)).thenReturn(mappedPerson);
        when(patientCrudRepository.save(mappedPatient)).thenReturn(savedPatient);
        when(patientPersistenceMapper.toModel(savedPatient)).thenReturn(patient);

        assertThat(patientRepository.create(patient)).isSameAs(patient);

        verify(personCrudRepository).save(mappedPerson);
        verify(patientCrudRepository).save(mappedPatient);
        assertThat(mappedPatient.getPersonEntity()).isSameAs(mappedPerson);
        assertThat(mappedPatient.getMedicalRecordNumber()).isEqualTo("MR-001");
    }

    @Test
    void create_whenPersonExistsWithoutPatient_shouldReusePersonWithoutOverwritingIt() {
        Patient patient = patient("MR-002");
        PersonEntity mappedPerson = person(patient);
        PersonEntity existingPerson = person(patient);
        existingPerson.setId(7L);
        PatientEntity mappedPatient = patientEntity(mappedPerson, "MR-002");
        PatientEntity savedPatient = patientEntity(existingPerson, "MR-002");
        when(patientPersistenceMapper.toEntity(patient)).thenReturn(mappedPatient);
        when(personCrudRepository.findByDocumentTypeAndDocumentNumber(DocumentType.DNI, "12345678"))
                .thenReturn(Optional.of(existingPerson));
        when(patientCrudRepository.save(mappedPatient)).thenAnswer(invocation -> {
            PatientEntity entity = invocation.getArgument(0);
            assertThat(entity.getPersonEntity()).isSameAs(existingPerson);
            return savedPatient;
        });
        when(patientPersistenceMapper.toModel(savedPatient)).thenReturn(patient);

        patientRepository.create(patient);

        verify(personCrudRepository, never()).save(any(PersonEntity.class));
        verify(patientCrudRepository).save(mappedPatient);
        assertThat(mappedPatient.getPersonEntity()).isSameAs(existingPerson);
        assertThat(existingPerson.getNames()).isEqualTo("Ana");
    }

    @Test
    void create_whenPersonIsAlreadyPatient_shouldReject() {
        Patient patient = patient("MR-001");
        PersonEntity existingPerson = person(patient);
        existingPerson.setPatientEntity(new PatientEntity());
        when(patientPersistenceMapper.toEntity(patient)).thenReturn(patientEntity(person(patient), "MR-001"));
        when(personCrudRepository.findByDocumentTypeAndDocumentNumber(DocumentType.DNI, "12345678"))
                .thenReturn(Optional.of(existingPerson));

        assertThatThrownBy(() -> patientRepository.create(patient))
                .isInstanceOf(PatientAlreadyExistsException.class)
                .hasMessage("The person is already a patient");

        verifyNoInteractions(patientCrudRepository);
    }

    @Test
    void create_whenPersonalDataDiffers_shouldReject() {
        Patient patient = patient("MR-001");
        PersonEntity existingPerson = person(patient);
        existingPerson.setEmail("other@example.com");
        when(patientPersistenceMapper.toEntity(patient)).thenReturn(patientEntity(person(patient), "MR-001"));
        when(personCrudRepository.findByDocumentTypeAndDocumentNumber(DocumentType.DNI, "12345678"))
                .thenReturn(Optional.of(existingPerson));

        assertThatThrownBy(() -> patientRepository.create(patient))
                .isInstanceOf(PersonDataConflictException.class)
                .hasMessage("Personal data does not match the existing person");

        verify(personCrudRepository, never()).save(any(PersonEntity.class));
        verifyNoInteractions(patientCrudRepository);
    }

    @Test
    void update_shouldUpdatePersonFieldsAndMedicalRecordAndPreserveIdentity() {
        Patient update = patient("MR-999");
        update.setId(7L);
        PersonEntity existingPerson = person(update);
        existingPerson.setId(7L);
        PatientEntity existingPatient = patientEntity(existingPerson, "MR-001");
        PatientEntity mappedPatient = patientEntity(person(update), "MR-999");
        PatientEntity savedPatient = patientEntity(existingPerson, "MR-999");
        when(patientCrudRepository.findById(7L)).thenReturn(Optional.of(existingPatient));
        when(patientPersistenceMapper.toEntity(update)).thenReturn(mappedPatient);
        when(patientCrudRepository.save(existingPatient)).thenReturn(savedPatient);
        when(patientPersistenceMapper.toModel(savedPatient)).thenReturn(update);

        assertThat(patientRepository.update(update)).isSameAs(update);

        verify(personCrudRepository).save(existingPerson);
        verify(patientCrudRepository).save(existingPatient);
        assertThat(existingPatient.getPersonEntity()).isSameAs(existingPerson);
        assertThat(existingPatient.getMedicalRecordNumber()).isEqualTo("MR-999");
        assertThat(existingPerson.getId()).isEqualTo(7L);
        assertThat(existingPerson.getNames()).isEqualTo(update.getNames());
    }

    @Test
    void update_whenPatientDoesNotExist_shouldReject() {
        Patient update = patient("MR-001");
        update.setId(7L);
        when(patientCrudRepository.findById(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientRepository.update(update))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Patient not found");

        verify(patientCrudRepository, never()).save(any(PatientEntity.class));
        verifyNoInteractions(personCrudRepository, patientPersistenceMapper);
    }

    private Patient patient(String medicalRecordNumber) {
        return Patient.builder()
                .documentType(DocumentType.DNI)
                .documentNumber("12345678")
                .names("Ana")
                .surnames("Lopez")
                .dateOfBirth(LocalDate.of(1990, 1, 15))
                .email("ana@example.com")
                .phoneNumber("999999999")
                .medicalRecordNumber(medicalRecordNumber)
                .build();
    }

    private PersonEntity person(Patient patient) {
        PersonEntity entity = new PersonEntity();
        entity.setDocumentType(patient.getDocumentType());
        entity.setDocumentNumber(patient.getDocumentNumber());
        entity.setNames(patient.getNames());
        entity.setSurnames(patient.getSurnames());
        entity.setDateOfBirth(patient.getDateOfBirth());
        entity.setEmail(patient.getEmail());
        entity.setPhoneNumber(patient.getPhoneNumber());
        return entity;
    }

    private PatientEntity patientEntity(PersonEntity person, String medicalRecordNumber) {
        PatientEntity entity = new PatientEntity();
        entity.setPersonEntity(person);
        entity.setId(person.getId());
        entity.setMedicalRecordNumber(medicalRecordNumber);
        return entity;
    }
}
