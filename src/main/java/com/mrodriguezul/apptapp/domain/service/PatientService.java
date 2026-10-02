package com.mrodriguezul.apptapp.domain.service;

import com.mrodriguezul.apptapp.domain.model.Patient;
import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;
import com.mrodriguezul.apptapp.persistence.adapter.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class PatientService {

    private final PatientRepository pacienteRepository;

    @Autowired
    public PatientService(PatientRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    public Page<Patient> getAll(int page, int size) {
        return pacienteRepository.findAll(page, size);
    }

    public Page<Patient> getAllByDocumentTypeAndDocumentNumber(DocumentType documentType, String documentNumber, int page, int size, String sortBy, String sortDir) {
        return pacienteRepository.findAllByDocumentTypeAndDocumentNumber(documentType, documentNumber, page, size, sortBy, sortDir);
    }
}
