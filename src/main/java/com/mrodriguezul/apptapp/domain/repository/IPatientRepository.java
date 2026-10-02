package com.mrodriguezul.apptapp.domain.repository;

import com.mrodriguezul.apptapp.domain.model.Patient;
import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;
import org.springframework.data.domain.Page;

public interface IPatientRepository {
    Page<Patient> findAll(int page, int size);
    Page<Patient> findAllByDocumentTypeAndDocumentNumber(DocumentType documentType, String documentNumber, int page, int size, String sortBy, String sortDir);
}
