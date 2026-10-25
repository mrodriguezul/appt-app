package com.mrodriguezul.apptapp.domain.service;

import com.mrodriguezul.apptapp.domain.exception.DoctorHasAppointmentsException;
import com.mrodriguezul.apptapp.domain.exception.PersonDataConflictException;
import com.mrodriguezul.apptapp.domain.exception.ResourceNotFoundException;
import com.mrodriguezul.apptapp.domain.model.Doctor;
import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;
import com.mrodriguezul.apptapp.domain.repository.IAppointmentRepository;
import com.mrodriguezul.apptapp.domain.repository.IDoctorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {
    @Mock
    private IDoctorRepository doctorRepository;
    @Mock
    private IAppointmentRepository appointmentRepository;
    @InjectMocks
    private DoctorService doctorService;

    @Test
    void getAll_shouldDelegateAndReturnDoctors() {
        List<Doctor> doctors = List.of(doctor(1L));
        when(doctorRepository.findAll()).thenReturn(doctors);

        assertThat(doctorService.getAll()).isSameAs(doctors);

        verify(doctorRepository).findAll();
    }

    @Test
    void getAllByNameOrSurname_shouldDelegateSearch() {
        List<Doctor> doctors = List.of(doctor(1L));
        when(doctorRepository.findAllByNameOrSurname("Ana", "Lopez")).thenReturn(doctors);

        assertThat(doctorService.getAllByNameOrSurname("Ana", "Lopez")).isSameAs(doctors);

        verify(doctorRepository).findAllByNameOrSurname("Ana", "Lopez");
    }

    @Test
    void getAllBySpeciality_shouldDelegateSearch() {
        List<Doctor> doctors = List.of(doctor(1L));
        when(doctorRepository.findAllBySpeciality(3L)).thenReturn(doctors);

        assertThat(doctorService.getAllBySpeciality(3L)).isSameAs(doctors);

        verify(doctorRepository).findAllBySpeciality(3L);
    }

    @Test
    void getDoctor_shouldDelegateLookupById() {
        Doctor doctor = doctor(10L);
        when(doctorRepository.findById(10L)).thenReturn(Optional.of(doctor));

        assertThat(doctorService.getDoctor(10L)).containsSame(doctor);

        verify(doctorRepository).findById(10L);
    }

    @Test
    void getAllByDocumentNumber_shouldDelegateLookup() {
        Doctor doctor = doctor(10L);
        when(doctorRepository.findByDocumentNumber("12345678")).thenReturn(Optional.of(doctor));

        assertThat(doctorService.getAllByDocumentNumber("12345678")).containsSame(doctor);

        verify(doctorRepository).findByDocumentNumber("12345678");
    }

    @Test
    void getDoctorByDocumentTypeAndDocumentNumber_shouldDelegateLookup() {
        Doctor doctor = doctor(10L);
        when(doctorRepository.findByDocumentTypeAndDocumentNumber(DocumentType.DNI, "12345678"))
                .thenReturn(Optional.of(doctor));

        assertThat(doctorService.getDoctorByDocumentTypeAndDocumentNumber(DocumentType.DNI, "12345678"))
                .containsSame(doctor);

        verify(doctorRepository).findByDocumentTypeAndDocumentNumber(DocumentType.DNI, "12345678");
    }

    @Test
    void create_shouldDelegateNewDoctor() {
        Doctor doctor = doctor(null);
        when(doctorRepository.save(doctor)).thenReturn(doctor);

        assertThat(doctorService.create(doctor)).isSameAs(doctor);

        verify(doctorRepository).save(doctor);
    }

    @Test
    void create_whenDoctorHasId_shouldRejectAndNotDelegate() {
        Doctor doctor = doctor(1L);

        assertThatThrownBy(() -> doctorService.create(doctor))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A new doctor must not have an ID");

        verifyNoInteractions(doctorRepository, appointmentRepository);
    }

    @Test
    void update_shouldSetRequestedIdAndDelegate() {
        Doctor existing = doctor(10L);
        Doctor update = doctor(null);
        when(doctorRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(doctorRepository.save(update)).thenReturn(update);

        assertThat(doctorService.update(10L, update)).isSameAs(update);
        assertThat(update.getId()).isEqualTo(10L);

        verify(doctorRepository).findById(10L);
        verify(doctorRepository).save(update);
    }

    @Test
    void update_whenDoctorDoesNotExist_shouldRejectAndNotSave() {
        Doctor update = doctor(null);
        when(doctorRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> doctorService.update(10L, update))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Doctor not found");

        verify(doctorRepository, never()).save(any());
    }

    @Test
    void update_whenIdIsInvalid_shouldRejectBeforeLookup() {
        Doctor update = doctor(null);

        assertThatThrownBy(() -> doctorService.update(0L, update))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid doctor ID");

        verifyNoInteractions(doctorRepository);
    }

    @Test
    void update_whenDocumentTypeChanges_shouldRejectAndNotSave() {
        Doctor existing = doctor(10L);
        Doctor update = doctor(null);
        update.setDocumentType(DocumentType.PASSPORT);
        when(doctorRepository.findById(10L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> doctorService.update(10L, update))
                .isInstanceOf(PersonDataConflictException.class)
                .hasMessage("Doctor identification cannot be changed");

        verify(doctorRepository, never()).save(any());
    }

    @Test
    void update_whenDocumentNumberChanges_shouldRejectAndNotSave() {
        Doctor existing = doctor(10L);
        Doctor update = doctor(null);
        update.setDocumentNumber("87654321");
        when(doctorRepository.findById(10L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> doctorService.update(10L, update))
                .isInstanceOf(PersonDataConflictException.class)
                .hasMessage("Doctor identification cannot be changed");

        verify(doctorRepository, never()).save(any());
    }

    @Test
    void delete_whenDoctorExistsWithoutAppointments_shouldDelete() {
        when(doctorRepository.existsById(10L)).thenReturn(true);
        when(appointmentRepository.existsByDoctorId(10L)).thenReturn(false);

        doctorService.delete(10L);

        verify(doctorRepository).existsById(10L);
        verify(appointmentRepository).existsByDoctorId(10L);
        verify(doctorRepository).delete(10L);
    }

    @Test
    void delete_whenDoctorDoesNotExist_shouldRejectAndNotDelete() {
        when(doctorRepository.existsById(10L)).thenReturn(false);

        assertThatThrownBy(() -> doctorService.delete(10L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Doctor not found");

        verify(doctorRepository, never()).delete(anyLong());
        verifyNoInteractions(appointmentRepository);
    }

    @Test
    void delete_whenIdIsInvalid_shouldRejectBeforeLookup() {
        assertThatThrownBy(() -> doctorService.delete(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid doctor ID");

        verifyNoInteractions(doctorRepository, appointmentRepository);
    }

    @Test
    void delete_whenDoctorHasAppointments_shouldRejectAndNotDelete() {
        when(doctorRepository.existsById(10L)).thenReturn(true);
        when(appointmentRepository.existsByDoctorId(10L)).thenReturn(true);

        assertThatThrownBy(() -> doctorService.delete(10L))
                .isInstanceOf(DoctorHasAppointmentsException.class)
                .hasMessage("Doctor cannot be deleted while appointments exist");

        verify(appointmentRepository).existsByDoctorId(10L);
        verify(doctorRepository, never()).delete(anyLong());
    }

    private Doctor doctor(Long id) {
        return Doctor.builder()
                .id(id)
                .documentType(DocumentType.DNI)
                .documentNumber("12345678")
                .names("Ana")
                .surnames("Lopez")
                .build();
    }
}
