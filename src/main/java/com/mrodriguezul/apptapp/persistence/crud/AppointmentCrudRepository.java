package com.mrodriguezul.apptapp.persistence.crud;

import com.mrodriguezul.apptapp.persistence.entity.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.time.LocalDateTime;

public interface AppointmentCrudRepository extends JpaRepository<AppointmentEntity, Long> {
    boolean existsByDoctorEntity_IdAndAppointmentDate(Long doctorId, LocalDateTime appointmentDate);
    List<AppointmentEntity> findAllByOrderByAppointmentDateAsc();
}
