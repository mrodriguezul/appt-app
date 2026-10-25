package com.mrodriguezul.apptapp.domain.service;

import com.mrodriguezul.apptapp.domain.exception.DoctorHasAppointmentsException;
import com.mrodriguezul.apptapp.domain.exception.PersonDataConflictException;
import com.mrodriguezul.apptapp.domain.exception.ResourceNotFoundException;
import com.mrodriguezul.apptapp.domain.model.Doctor;
import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;
import com.mrodriguezul.apptapp.domain.repository.IDoctorRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class DoctorService {

    private final IDoctorRepository doctorRepository;

    public DoctorService(IDoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public List<Doctor> getAll() {
        return doctorRepository.findAll();
    }

    public List<Doctor> getAllByNameOrSurname(String names, String surnames) {
        return doctorRepository.findAllByNameOrSurname(names, surnames);
    }

    public List<Doctor> getAllBySpeciality(Long specialityId) {
        return doctorRepository.findAllBySpeciality(specialityId);
    }

    public Optional<Doctor> getAllByDocumentNumber(String documentNumber){
        return doctorRepository.findByDocumentNumber(documentNumber);
    }

    public Optional<Doctor> getDoctor(Long id) {
        return doctorRepository.findById(id);
    }

    public Optional<Doctor> getDoctorByDocumentTypeAndDocumentNumber(DocumentType documentType, String documentNumber){
        return doctorRepository.findByDocumentTypeAndDocumentNumber(documentType, documentNumber);
    }

    @Transactional
    public Doctor create(Doctor doctor) {
        if (doctor.getId() != null) {
            throw new IllegalArgumentException("A new doctor must not have an ID");
        }
        return doctorRepository.save(doctor);
    }

    @Transactional
    public Doctor update(Long id, Doctor doctor) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid doctor ID");
        }

        Doctor existing = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        if (doctor.getDocumentType() != existing.getDocumentType() || !Objects.equals(doctor.getDocumentNumber(), existing.getDocumentNumber())) {
            throw new PersonDataConflictException("Doctor identification cannot be changed");
        }

        doctor.setId(id);
        return doctorRepository.save(doctor);
    }

    @Transactional
    public void delete(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid doctor ID");
        }
        if (!doctorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Doctor not found");
        }

        try {
            doctorRepository.delete(id);
        } catch (DataIntegrityViolationException ex) {
            throw new DoctorHasAppointmentsException("Doctor cannot be deleted while appointments exist", ex);
        }
    }
}
