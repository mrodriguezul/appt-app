package com.mrodriguezul.apptapp.domain.repository;

import com.mrodriguezul.apptapp.domain.model.Appointment;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

public interface IAppointmentRepository {
    Appointment create(Appointment appointment);
    Appointment update(Long id, Appointment appointment);
    List<Appointment> findAll();
    Optional<Appointment> findById(Long id);
    boolean existsByDoctorIdAndAppointmentDate(Long doctorId, LocalDateTime appointmentDate);
}
