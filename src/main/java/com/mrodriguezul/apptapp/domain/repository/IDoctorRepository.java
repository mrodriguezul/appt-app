package com.mrodriguezul.apptapp.domain.repository;

import com.mrodriguezul.apptapp.domain.model.Doctor;
import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;

import java.util.List;
import java.util.Optional;

public interface IDoctorRepository {
    List<Doctor> findAll();
    List<Doctor> findAllByNameOrSurname(String names, String surnames);
    List<Doctor> findAllBySpeciality(Long specialityId);
    Optional<Doctor> findByDocumentNumber(String identificationNumber);
    Optional<Doctor> findById(Long id);
    Optional<Doctor> findByDocumentTypeAndDocumentNumber(DocumentType documentType, String documentNumber);
    Doctor save(Doctor doctor);
    void delete(Long id);
    boolean existsById(Long id);
}

