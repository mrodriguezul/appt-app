package com.mrodriguezul.apptapp.domain.service;

import com.mrodriguezul.apptapp.domain.model.Appointment;
import com.mrodriguezul.apptapp.domain.repository.IAppointmentRepository;
import com.mrodriguezul.apptapp.domain.repository.IDoctorRepository;
import com.mrodriguezul.apptapp.domain.repository.IPatientRepository;
import com.mrodriguezul.apptapp.domain.exception.AppointmentConflictException;
import com.mrodriguezul.apptapp.domain.exception.InvalidAppointmentException;
import com.mrodriguezul.apptapp.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {
    @Mock
    private IAppointmentRepository appointmentRepository;
    @Mock
    private IDoctorRepository doctorRepository;
    @Mock
    private IPatientRepository patientRepository;
    @InjectMocks
    private AppointmentService appointmentService;

    private final Clock clock = Clock.fixed(Instant.parse("2026-10-08T20:00:00Z"), ZoneOffset.UTC);

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        appointmentService = new AppointmentService(
                appointmentRepository, doctorRepository, patientRepository, clock);
    }

    @Test
    void createsAppointmentWhenDoctorPatientAndSlotAreAvailable() {
        Appointment appointment = validAppointment();
        when(doctorRepository.existsById(1L)).thenReturn(true);
        when(patientRepository.existsById(2L)).thenReturn(true);
        when(appointmentRepository.existsByDoctorIdAndAppointmentDate(1L, appointment.getAppointmentDate()))
                .thenReturn(false);
        when(appointmentRepository.create(appointment)).thenReturn(appointment);

        assertSame(appointment, appointmentService.create(appointment));
        verify(appointmentRepository).create(appointment);
    }

    @Test
    void rejectsUnknownDoctor() {
        Appointment appointment = validAppointment();
        when(doctorRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> appointmentService.create(appointment));
        verify(appointmentRepository, never()).create(any());
    }

    @Test
    void rejectsUnknownPatient() {
        Appointment appointment = validAppointment();
        when(doctorRepository.existsById(1L)).thenReturn(true);
        when(patientRepository.existsById(2L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> appointmentService.create(appointment));
        verify(appointmentRepository, never()).create(any());
    }

    @Test
    void rejectsPastDate() {
        Appointment appointment = new Appointment(
                null, 1L, 2L, LocalDateTime.of(2026, 10, 8, 19, 59), "Consulta");

        assertThrows(InvalidAppointmentException.class, () -> appointmentService.create(appointment));
        verifyNoInteractions(doctorRepository, patientRepository, appointmentRepository);
    }

    @Test
    void rejectsOccupiedSlot() {
        Appointment appointment = validAppointment();
        when(doctorRepository.existsById(1L)).thenReturn(true);
        when(patientRepository.existsById(2L)).thenReturn(true);
        when(appointmentRepository.existsByDoctorIdAndAppointmentDate(1L, appointment.getAppointmentDate()))
                .thenReturn(true);

        assertThrows(AppointmentConflictException.class, () -> appointmentService.create(appointment));
        verify(appointmentRepository, never()).create(any());
    }

    @Test
    void rejectsAppointmentWithPreassignedId() {
        Appointment appointment = new Appointment(
                99L, 1L, 2L, LocalDateTime.of(2026, 10, 8, 21, 0), "Consulta");

        assertThrows(InvalidAppointmentException.class, () -> appointmentService.create(appointment));
        verifyNoInteractions(doctorRepository, patientRepository, appointmentRepository);
    }

    @Test
    void updatesAppointmentSuccessfully() {
        Appointment existing = appointment(10L, 1L, 2L, 21, 0, 0);
        Appointment update = appointment(null, 1L, 2L, 22, 0, 0);
        when(appointmentRepository.findById(10L)).thenReturn(java.util.Optional.of(existing));
        when(doctorRepository.existsById(1L)).thenReturn(true);
        when(patientRepository.existsById(2L)).thenReturn(true);
        when(appointmentRepository.existsByDoctorIdAndAppointmentDate(1L, update.getAppointmentDate()))
                .thenReturn(false);
        when(appointmentRepository.update(10L, update)).thenReturn(update);

        assertSame(update, appointmentService.update(10L, update));
        assertEquals(10L, update.getId());
        verify(appointmentRepository).update(10L, update);
    }

    @Test
    void updatesAppointmentWithoutChangingDoctorOrDate() {
        Appointment existing = appointment(10L, 1L, 2L, 21, 0, 0);
        Appointment update = appointment(null, 1L, 2L, 21, 0, 0);
        when(appointmentRepository.findById(10L)).thenReturn(java.util.Optional.of(existing));
        when(doctorRepository.existsById(1L)).thenReturn(true);
        when(patientRepository.existsById(2L)).thenReturn(true);
        when(appointmentRepository.update(10L, update)).thenReturn(update);

        assertSame(update, appointmentService.update(10L, update));

        verify(appointmentRepository).update(10L, update);
        verify(appointmentRepository, never())
                .existsByDoctorIdAndAppointmentDate(anyLong(), any());
    }

    @Test
    void rejectsUpdateWhenAppointmentDoesNotExist() {
        Appointment update = appointment(null, 1L, 2L, 21, 0, 0);
        when(appointmentRepository.findById(10L)).thenReturn(java.util.Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> appointmentService.update(10L, update));
        verify(appointmentRepository, never()).update(anyLong(), any());
    }

    @Test
    void rejectsUpdateWhenDoctorDoesNotExist() {
        Appointment existing = appointment(10L, 1L, 2L, 21, 0, 0);
        Appointment update = appointment(null, 99L, 2L, 21, 0, 0);
        when(appointmentRepository.findById(10L)).thenReturn(java.util.Optional.of(existing));
        when(doctorRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> appointmentService.update(10L, update));
        verify(appointmentRepository, never()).update(anyLong(), any());
    }

    @Test
    void rejectsUpdateWhenPatientDoesNotExist() {
        Appointment existing = appointment(10L, 1L, 2L, 21, 0, 0);
        Appointment update = appointment(null, 1L, 99L, 21, 0, 0);
        when(appointmentRepository.findById(10L)).thenReturn(java.util.Optional.of(existing));
        when(doctorRepository.existsById(1L)).thenReturn(true);
        when(patientRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> appointmentService.update(10L, update));
        verify(appointmentRepository, never()).update(anyLong(), any());
    }

    @Test
    void rejectsUpdateWithPastDate() {
        Appointment update = appointment(null, 1L, 2L, 19, 59, 0);

        assertThrows(InvalidAppointmentException.class, () -> appointmentService.update(10L, update));
        verifyNoInteractions(doctorRepository, patientRepository, appointmentRepository);
    }

    @Test
    void rejectsUpdateWithNonZeroSeconds() {
        Appointment update = appointment(null, 1L, 2L, 21, 0, 1);

        assertThrows(InvalidAppointmentException.class, () -> appointmentService.update(10L, update));
        verifyNoInteractions(doctorRepository, patientRepository, appointmentRepository);
    }

    @Test
    void rejectsUpdateWithNonZeroNanoseconds() {
        Appointment update = appointment(null, 1L, 2L, 21, 0, 0);
        update.setAppointmentDate(update.getAppointmentDate().withNano(1));

        assertThrows(InvalidAppointmentException.class, () -> appointmentService.update(10L, update));
        verifyNoInteractions(doctorRepository, patientRepository, appointmentRepository);
    }

    @Test
    void rejectsUpdateWhenNewSlotConflictsWithAnotherAppointment() {
        Appointment existing = appointment(10L, 1L, 2L, 21, 0, 0);
        Appointment update = appointment(null, 1L, 2L, 22, 0, 0);
        when(appointmentRepository.findById(10L)).thenReturn(java.util.Optional.of(existing));
        when(doctorRepository.existsById(1L)).thenReturn(true);
        when(patientRepository.existsById(2L)).thenReturn(true);
        when(appointmentRepository.existsByDoctorIdAndAppointmentDate(1L, update.getAppointmentDate()))
                .thenReturn(true);

        assertThrows(AppointmentConflictException.class, () -> appointmentService.update(10L, update));
        verify(appointmentRepository, never()).update(anyLong(), any());
    }

    @Test
    void updatesAppointmentWhenDoctorChanges() {
        Appointment existing = appointment(10L, 1L, 2L, 21, 0, 0);
        Appointment update = appointment(null, 3L, 2L, 21, 0, 0);
        when(appointmentRepository.findById(10L)).thenReturn(java.util.Optional.of(existing));
        when(doctorRepository.existsById(3L)).thenReturn(true);
        when(patientRepository.existsById(2L)).thenReturn(true);
        when(appointmentRepository.existsByDoctorIdAndAppointmentDate(3L, update.getAppointmentDate()))
                .thenReturn(false);
        when(appointmentRepository.update(10L, update)).thenReturn(update);

        assertSame(update, appointmentService.update(10L, update));

        verify(appointmentRepository).existsByDoctorIdAndAppointmentDate(3L, update.getAppointmentDate());
        verify(appointmentRepository).update(10L, update);
    }

    private Appointment validAppointment() {
        return new Appointment(
                null, 1L, 2L,
                LocalDateTime.of(2026, 10, 8, 21, 0), "Consulta");
    }

    private Appointment appointment(
            Long id, Long doctorId, Long patientId, int hour, int minute, int second) {
        return new Appointment(
                id,
                doctorId,
                patientId,
                LocalDateTime.of(2026, 10, 8, hour, minute, second),
                "Consulta");
    }
}
