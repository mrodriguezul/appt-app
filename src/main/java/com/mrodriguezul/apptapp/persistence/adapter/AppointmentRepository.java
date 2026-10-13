package com.mrodriguezul.apptapp.persistence.adapter;

import com.mrodriguezul.apptapp.domain.exception.ResourceNotFoundException;
import com.mrodriguezul.apptapp.domain.model.Appointment;
import com.mrodriguezul.apptapp.domain.repository.IAppointmentRepository;
import com.mrodriguezul.apptapp.persistence.crud.AppointmentCrudRepository;
import com.mrodriguezul.apptapp.persistence.crud.DoctorCrudRepository;
import com.mrodriguezul.apptapp.persistence.crud.PatientCrudRepository;
import com.mrodriguezul.apptapp.persistence.entity.AppointmentEntity;
import com.mrodriguezul.apptapp.persistence.mapper.AppointmentPersistenceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class AppointmentRepository implements IAppointmentRepository {
    private final AppointmentCrudRepository appointmentCrudRepository;
    private final DoctorCrudRepository doctorCrudRepository;
    private final PatientCrudRepository patientCrudRepository;
    private final AppointmentPersistenceMapper appointmentPersistenceMapper;

    @Autowired
    public AppointmentRepository(
            AppointmentCrudRepository appointmentCrudRepository,
            DoctorCrudRepository doctorCrudRepository,
            PatientCrudRepository patientCrudRepository,
            AppointmentPersistenceMapper appointmentPersistenceMapper) {
        this.appointmentCrudRepository = appointmentCrudRepository;
        this.doctorCrudRepository = doctorCrudRepository;
        this.patientCrudRepository = patientCrudRepository;
        this.appointmentPersistenceMapper = appointmentPersistenceMapper;
    }

    @Override
    public Appointment create(Appointment appointment) {
        AppointmentEntity entity = appointmentPersistenceMapper.toEntity(appointment);
        entity.setDoctorEntity(doctorCrudRepository.getReferenceById(appointment.getDoctorId()));
        entity.setPatientEntity(patientCrudRepository.getReferenceById(appointment.getPatientId()));
        return appointmentPersistenceMapper.toModel(appointmentCrudRepository.save(entity));
    }

    @Override
    public Appointment update(Long id, Appointment appointment) {
        AppointmentEntity entity = appointmentCrudRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        appointmentPersistenceMapper.updateEntity(appointment, entity);
        entity.setDoctorEntity(doctorCrudRepository.getReferenceById(appointment.getDoctorId()));
        entity.setPatientEntity(patientCrudRepository.getReferenceById(appointment.getPatientId()));

        return appointmentPersistenceMapper.toModel(appointmentCrudRepository.save(entity));
    }

    @Override
    public List<Appointment> findAll() {
        return appointmentCrudRepository.findAllByOrderByAppointmentDateAsc()
                .stream()
                .map(appointmentPersistenceMapper::toModel)
                .toList();
    }

    @Override
    public Optional<Appointment> findById(Long id) {
        return appointmentCrudRepository.findById(id).map(appointmentPersistenceMapper::toModel);
    }

    @Override
    public boolean existsByDoctorIdAndAppointmentDate(Long doctorId, LocalDateTime appointmentDate) {
        return appointmentCrudRepository.existsByDoctorEntity_IdAndAppointmentDate(doctorId, appointmentDate);
    }

}
