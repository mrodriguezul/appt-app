package com.mrodriguezul.apptapp.domain.service;

import com.mrodriguezul.apptapp.domain.exception.PersonDataConflictException;
import com.mrodriguezul.apptapp.domain.exception.ResourceNotFoundException;
import com.mrodriguezul.apptapp.domain.model.Patient;
import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;
import com.mrodriguezul.apptapp.domain.repository.IPatientRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class PatientService {

    private final IPatientRepository patientRepository;

    public PatientService(IPatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public Page<Patient> getAll(int page, int size) {
        return patientRepository.findAll(page, size);
    }

    public Page<Patient> getAllByNameOrSurname(String names, String surnames, int page, int size, String sortBy, String sortDir) {
        return patientRepository.findAllByNameOrSurname(names, surnames, page, size, sortBy, sortDir);
    }

    public Patient getByDocumentTypeAndDocumentNumber(DocumentType documentType, String documentNumber) {
        return patientRepository.findByDocumentTypeAndDocumentNumber(documentType, documentNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Patient not found")
                );
    }

    @Transactional
    public Patient create(Patient patient) {
        if (patient.getId() != null) {
            throw new IllegalArgumentException("A new patient must not have an ID");
        }
        return patientRepository.create(patient);
    }

    @Transactional
    public Patient update(Long id, Patient patient) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid patient ID");
        }
        Patient existing = patientRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Patient not found")
                );

        if (patient.getDocumentType() != existing.getDocumentType() || !Objects.equals(patient.getDocumentNumber(),existing.getDocumentNumber())) {
            throw new PersonDataConflictException("Patient identification cannot be changed");
        }
        patient.setId(id);
        return patientRepository.update(patient);
    }
}
