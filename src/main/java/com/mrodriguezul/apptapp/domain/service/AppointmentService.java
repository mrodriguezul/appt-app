package com.mrodriguezul.apptapp.domain.service;

import com.mrodriguezul.apptapp.domain.model.Appointment;
import com.mrodriguezul.apptapp.domain.exception.AppointmentConflictException;
import com.mrodriguezul.apptapp.domain.exception.InvalidAppointmentException;
import com.mrodriguezul.apptapp.domain.exception.ResourceNotFoundException;
import com.mrodriguezul.apptapp.domain.repository.IAppointmentRepository;
import com.mrodriguezul.apptapp.domain.repository.IDoctorRepository;
import com.mrodriguezul.apptapp.domain.repository.IPatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {
    private final IAppointmentRepository appointmentRepository;
    private final IDoctorRepository doctorRepository;
    private final IPatientRepository patientRepository;
    private final Clock clock;

    @Autowired
    public AppointmentService(
            IAppointmentRepository appointmentRepository,
            IDoctorRepository doctorRepository,
            IPatientRepository patientRepository,
            Clock clock) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.clock = clock;
    }

    @Transactional
    public Appointment create(Appointment appointment) {
        if (appointment != null && appointment.getId() != null){
            throw new InvalidAppointmentException("Can't create an appointment with an existing ID");
        }
        validate(appointment);

        if (!doctorRepository.existsById(appointment.getDoctorId())) {
            throw new ResourceNotFoundException("Doctor does not exist");
        }
        if (!patientRepository.existsById(appointment.getPatientId())) {
            throw new ResourceNotFoundException("Patient does not exist");
        }
        if (appointmentRepository.existsByDoctorIdAndAppointmentDate(
                appointment.getDoctorId(), appointment.getAppointmentDate())) {
            throw new AppointmentConflictException("There is already an appointment for the doctor at that time");
        }

        return appointmentRepository.create(appointment);
    }

    @Transactional
    public Appointment update(Long id, Appointment appointment) {
        if (id == null || id <= 0) {
            throw new InvalidAppointmentException("Invalid appointment ID");
        }
        validate(appointment);
        validateMinutePrecision(appointment.getAppointmentDate());

        Appointment existingAppointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        if(!existingAppointment.getId().equals(id)) {
            throw new InvalidAppointmentException("Appointment ID mismatch");
        }
        appointment.setId(id);

        if (!doctorRepository.existsById(appointment.getDoctorId())) {
            throw new ResourceNotFoundException("Doctor dosn't exist");
        }
        if (!patientRepository.existsById(appointment.getPatientId())) {
            throw new ResourceNotFoundException("Patient dosn't exist");
        }
        boolean sameSlot = existingAppointment.getDoctorId().equals(appointment.getDoctorId())
                && existingAppointment.getAppointmentDate().equals(appointment.getAppointmentDate());
        if (!sameSlot && appointmentRepository.existsByDoctorIdAndAppointmentDate(
                appointment.getDoctorId(), appointment.getAppointmentDate())) {
            throw new AppointmentConflictException("There is already an appointment for the doctor at that time");
        }

        return appointmentRepository.update(id, appointment);
    }

    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }

    public Optional<Appointment> findById(Long id) {
        return appointmentRepository.findById(id);
    }

    private void validate(Appointment appointment) {
        if (appointment == null
                || appointment.getDoctorId() == null
                || appointment.getPatientId() == null
                || appointment.getAppointmentDate() == null
                || appointment.getReason() == null
                || appointment.getReason().isBlank()
                || !appointment.getAppointmentDate().isAfter(LocalDateTime.now(clock))) {
            throw new InvalidAppointmentException("Invalid appointment data");
        }
    }

    private void validateMinutePrecision(LocalDateTime date) {
        if (date.getSecond() != 0 || date.getNano() != 0) {
            throw new InvalidAppointmentException(
                    "The appointment date must be precise to the minute (seconds and nanoseconds must be zero)"
            );
        }
    }
}
