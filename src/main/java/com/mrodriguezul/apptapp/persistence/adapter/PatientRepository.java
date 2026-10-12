package com.mrodriguezul.apptapp.persistence.adapter;

import com.mrodriguezul.apptapp.domain.exception.PatientAlreadyExistsException;
import com.mrodriguezul.apptapp.domain.exception.PersonDataConflictException;
import com.mrodriguezul.apptapp.domain.model.Patient;
import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;
import com.mrodriguezul.apptapp.domain.exception.ResourceNotFoundException;
import com.mrodriguezul.apptapp.domain.repository.IPatientRepository;
import com.mrodriguezul.apptapp.persistence.crud.PatientCrudRepository;
import com.mrodriguezul.apptapp.persistence.crud.PersonCrudRepository;
import com.mrodriguezul.apptapp.persistence.entity.PatientEntity;
import com.mrodriguezul.apptapp.persistence.entity.PersonEntity;
import com.mrodriguezul.apptapp.persistence.mapper.PatientPersistenceMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;

@Repository
public class PatientRepository implements IPatientRepository {

    private final PatientCrudRepository patientCrudRepository;
    private final PersonCrudRepository personCrudRepository;
    private final PatientPersistenceMapper patientPersistenceMapper;

    public PatientRepository(
            PatientCrudRepository patientCrudRepository,
            PersonCrudRepository personCrudRepository,
            PatientPersistenceMapper patientPersistenceMapper) {
        this.patientCrudRepository = patientCrudRepository;
        this.personCrudRepository = personCrudRepository;
        this.patientPersistenceMapper = patientPersistenceMapper;
    }

    @Override
    public Page<Patient> findAll(int page, int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return patientCrudRepository.findAll(pageable).map(patientPersistenceMapper::toModel);
    }

    @Override
    public Page<Patient> findAllByDocumentTypeAndDocumentNumber(DocumentType documentType, String documentNumber, int page, int size, String sortBy, String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return patientCrudRepository.findAllByPersonEntity_DocumentTypeAndPersonEntity_DocumentNumberOrderByIdAsc(documentType, documentNumber, pageable).map(patientPersistenceMapper::toModel);
    }

    @Override
    public boolean existsById(Long id) {
        return patientCrudRepository.existsById(id);
    }

    @Override
    public Optional<Patient> findById(Long id) {
        return patientCrudRepository.findById(id).map(patientPersistenceMapper::toModel);
    }

    @Override
    public Optional<Patient> findByDocumentTypeAndDocumentNumber(DocumentType documentType, String documentNumber) {
        return patientCrudRepository.findByPersonEntity_DocumentTypeAndPersonEntity_DocumentNumber(documentType, documentNumber).map(patientPersistenceMapper::toModel);
    }

    @Override
    public Patient create(Patient patient) {
        PatientEntity patientEntity = patientPersistenceMapper.toEntity(patient);
        PersonEntity mappedPersonEntity = patientEntity.getPersonEntity();

        Optional<PersonEntity> existingPerson = personCrudRepository.findByDocumentTypeAndDocumentNumber(patient.getDocumentType(), patient.getDocumentNumber());
        PersonEntity personEntity;

        if(existingPerson.isPresent()){
            personEntity = existingPerson.get();
            if (personEntity.getPatientEntity() != null) {
                throw new PatientAlreadyExistsException("The person is already a patient");
            }
            validatePersonalData(patient, personEntity);
        }else{
            personEntity = personCrudRepository.save(mappedPersonEntity);
        }
        patientEntity.setPersonEntity(personEntity);
        return patientPersistenceMapper.toModel(patientCrudRepository.save(patientEntity));
    }

    @Override
    public Patient update(Patient patient) {
        PatientEntity existingPatient = patientCrudRepository.findById(patient.getId()).orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        PersonEntity existingPerson = existingPatient.getPersonEntity();

        PatientEntity patientEntityMapped = patientPersistenceMapper.toEntity(patient);
        PersonEntity personEntityMapped = patientEntityMapped.getPersonEntity();

        existingPerson.setNames(personEntityMapped.getNames());
        existingPerson.setSurnames(personEntityMapped.getSurnames());
        existingPerson.setDateOfBirth(personEntityMapped.getDateOfBirth());
        existingPerson.setEmail(personEntityMapped.getEmail());
        existingPerson.setPhoneNumber(personEntityMapped.getPhoneNumber());
        existingPatient.setMedicalRecordNumber(patient.getMedicalRecordNumber());

        personCrudRepository.save(existingPerson);
        return patientPersistenceMapper.toModel(patientCrudRepository.save(existingPatient));
    }

    private void validatePersonalData(Patient patient, PersonEntity personEntity) {
        if (!Objects.equals(patient.getNames(), personEntity.getNames())
                || !Objects.equals(patient.getSurnames(), personEntity.getSurnames())
                || !Objects.equals(patient.getDateOfBirth(), personEntity.getDateOfBirth())
                || !Objects.equals(patient.getEmail(), personEntity.getEmail())
                || !Objects.equals(patient.getPhoneNumber(), personEntity.getPhoneNumber())) {
            throw new PersonDataConflictException("Personal data does not match the existing person");
        }
    }
}
