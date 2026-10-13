package com.mrodriguezul.apptapp.domain.service;

import com.mrodriguezul.apptapp.domain.exception.PersonDataConflictException;
import com.mrodriguezul.apptapp.domain.exception.ResourceNotFoundException;
import com.mrodriguezul.apptapp.domain.model.Patient;
import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;
import com.mrodriguezul.apptapp.domain.repository.IPatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {
    @Mock
    private IPatientRepository patientRepository;
    @InjectMocks
    private PatientService patientService;

    @Test
    void create_shouldDelegateNewPatient() {
        Patient patient = patient(null, "12345678", "MR-001");
        when(patientRepository.create(patient)).thenReturn(patient);

        assertThat(patientService.create(patient)).isSameAs(patient);

        verify(patientRepository).create(patient);
    }

    @Test
    void create_whenPatientHasId_shouldRejectAndNotDelegate() {
        Patient patient = patient(1L, "12345678", "MR-001");

        assertThatThrownBy(() -> patientService.create(patient))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A new patient must not have an ID");

        verifyNoInteractions(patientRepository);
    }

    @Test
    void update_shouldSetRequestedIdAndDelegate() {
        Patient existing = patient(10L, "12345678", "MR-001");
        Patient update = patient(null, "12345678", "MR-002");
        when(patientRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(patientRepository.update(update)).thenReturn(update);

        assertThat(patientService.update(10L, update)).isSameAs(update);
        assertThat(update.getId()).isEqualTo(10L);

        verify(patientRepository).findById(10L);
        verify(patientRepository).update(update);
    }

    @Test
    void update_whenIdDoesNotExist_shouldReject() {
        Patient update = patient(null, "12345678", "MR-002");
        when(patientRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.update(10L, update))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Patient not found");

        verify(patientRepository, never()).update(any());
    }

    @Test
    void update_whenIdIsInvalid_shouldRejectBeforeLookup() {
        Patient update = patient(null, "12345678", "MR-002");

        assertThatThrownBy(() -> patientService.update(0L, update))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid patient ID");

        verifyNoInteractions(patientRepository);
    }

    @Test
    void update_whenDocumentTypeChanges_shouldReject() {
        Patient existing = patient(10L, "12345678", "MR-001");
        Patient update = patient(null, "12345678", "MR-002");
        update.setDocumentType(DocumentType.PASSPORT);
        when(patientRepository.findById(10L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> patientService.update(10L, update))
                .isInstanceOf(PersonDataConflictException.class)
                .hasMessage("Patient identification cannot be changed");

        verify(patientRepository, never()).update(any());
    }

    @Test
    void update_whenDocumentNumberChanges_shouldReject() {
        Patient existing = patient(10L, "12345678", "MR-001");
        Patient update = patient(null, "87654321", "MR-002");
        when(patientRepository.findById(10L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> patientService.update(10L, update))
                .isInstanceOf(PersonDataConflictException.class)
                .hasMessage("Patient identification cannot be changed");

        verify(patientRepository, never()).update(any());
    }

    @Test
    void getAll_shouldDelegateAndReturnPage() {
        Patient patient = patient(10L, "12345678", "MR-001");
        Page<Patient> page = new PageImpl<>(List.of(patient), PageRequest.of(1, 5), 6);
        when(patientRepository.findAll(1, 5)).thenReturn(page);

        assertThat(patientService.getAll(1, 5)).isSameAs(page);

        verify(patientRepository).findAll(1, 5);
    }

    @Test
    void getAllByNameOrSurname_shouldDelegateSearch() {
        Page<Patient> page = new PageImpl<>(List.of());
        when(patientRepository.findAllByNameOrSurname("Ana", "Lopez", 0, 10, "id", "ASC"))
                .thenReturn(page);

        assertThat(patientService.getAllByNameOrSurname("Ana", "Lopez", 0, 10, "id", "ASC"))
                .isSameAs(page);

        verify(patientRepository).findAllByNameOrSurname("Ana", "Lopez", 0, 10, "id", "ASC");
    }

    @Test
    void getByDocument_whenPatientExists_shouldReturnPatient() {
        Patient patient = patient(10L, "12345678", "MR-001");
        when(patientRepository.findByDocumentTypeAndDocumentNumber(DocumentType.DNI, "12345678"))
                .thenReturn(Optional.of(patient));

        assertThat(patientService.getByDocumentTypeAndDocumentNumber(DocumentType.DNI, "12345678"))
                .isSameAs(patient);
    }

    @Test
    void getByDocument_whenPatientDoesNotExist_shouldReject() {
        when(patientRepository.findByDocumentTypeAndDocumentNumber(DocumentType.DNI, "12345678"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.getByDocumentTypeAndDocumentNumber(DocumentType.DNI, "12345678"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Patient not found");
    }

    private Patient patient(Long id, String documentNumber, String medicalRecordNumber) {
        return Patient.builder()
                .id(id)
                .documentType(DocumentType.DNI)
                .documentNumber(documentNumber)
                .names("Ana")
                .surnames("Lopez")
                .dateOfBirth(LocalDate.of(1990, 1, 15))
                .email("ana@example.com")
                .phoneNumber("999999999")
                .medicalRecordNumber(medicalRecordNumber)
                .build();
    }
}
