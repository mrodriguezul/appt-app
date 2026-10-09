package com.mrodriguezul.apptapp.persistence.adapter;

import com.mrodriguezul.apptapp.domain.model.Patient;
import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;
import com.mrodriguezul.apptapp.domain.repository.IPatientRepository;
import com.mrodriguezul.apptapp.persistence.crud.PatientCrudRepository;
import com.mrodriguezul.apptapp.persistence.crud.PatientPagSortRepository;
import com.mrodriguezul.apptapp.persistence.mapper.PatientPersistenceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class PatientRepository implements IPatientRepository {

    private final PatientPagSortRepository patientPagSortRepository;
    private final PatientCrudRepository patientCrudRepository;
    private final PatientPersistenceMapper patientPersistenceMapper;

    @Autowired
    public PatientRepository(
            PatientPagSortRepository patientPagSortRepository,
            PatientCrudRepository patientCrudRepository,
            PatientPersistenceMapper patientPersistenceMapper) {
        this.patientPagSortRepository = patientPagSortRepository;
        this.patientCrudRepository = patientCrudRepository;
        this.patientPersistenceMapper = patientPersistenceMapper;
    }

    @Override
    public Page<Patient> findAll(int page, int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);

        return patientPagSortRepository.findAll(pageable).map(patientPersistenceMapper::toPaciente);
    }

    @Override
    public Page<Patient> findAllByDocumentTypeAndDocumentNumber(DocumentType documentType, String documentNumber, int page, int size, String sortBy, String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return patientPagSortRepository.findAllByPersonEntity_DocumentTypeAndPersonEntity_DocumentNumberOrderByIdAsc(documentType, documentNumber, pageable).map(patientPersistenceMapper::toPaciente);
    }

    @Override
    public boolean existsById(Long id) {
        return patientCrudRepository.existsById(id);
    }
}
