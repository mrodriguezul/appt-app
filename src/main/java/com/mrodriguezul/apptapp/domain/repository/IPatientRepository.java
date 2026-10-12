package com.mrodriguezul.apptapp.domain.repository;

import com.mrodriguezul.apptapp.domain.model.Patient;
import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface IPatientRepository {
    Page<Patient> findAll(int page, int size);
    Optional<Patient> findByDocumentTypeAndDocumentNumber(DocumentType documentType, String documentNumber);
    Page<Patient> findAllByNameOrSurname(String names, String surnames, int page, int size, String sortBy, String sortDir);
    boolean existsById(Long id);
    Optional<Patient> findById(Long id);
    Patient create(Patient patient);
    Patient update(Patient patient);
}
