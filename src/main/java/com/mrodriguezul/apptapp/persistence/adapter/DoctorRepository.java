package com.mrodriguezul.apptapp.persistence.adapter;

import com.mrodriguezul.apptapp.domain.model.Doctor;
import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;
import com.mrodriguezul.apptapp.domain.repository.IDoctorRepository;
import com.mrodriguezul.apptapp.persistence.crud.DoctorCrudRepository;
import com.mrodriguezul.apptapp.persistence.crud.PersonCrudRepository;
import com.mrodriguezul.apptapp.persistence.entity.DoctorEntity;
import com.mrodriguezul.apptapp.persistence.entity.PersonEntity;
import com.mrodriguezul.apptapp.persistence.mapper.DoctorPersistenceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class DoctorRepository implements IDoctorRepository {
    @Autowired
    private DoctorCrudRepository doctorCrudRepository;

    @Autowired
    private PersonCrudRepository personCrudRepository;

    @Autowired
    private DoctorPersistenceMapper mapper;

    @Override
    public List<Doctor> findAll() {
        return ((List<DoctorEntity>) doctorCrudRepository.findAllByOrderByIdAsc())
                .stream()
                .map(mapper::toDoctor)
                .toList();
    }

    @Override
    public List<Doctor> findAllByNameOrSurname(String names, String surnames) {
        return ((List<DoctorEntity>) doctorCrudRepository.findAllByPersonEntity_namesContainingIgnoreCaseOrPersonEntity_surnamesContainingIgnoreCaseOrderByIdAsc(names, surnames))
                .stream()
                .map(mapper::toDoctor)
                .toList();
    }

    @Override
    public List<Doctor> findAllBySpeciality(Long specialityId) {
        return ((List<DoctorEntity>) doctorCrudRepository.findAllBySpecialityEntity_IdOrderByIdAsc(specialityId))
                .stream()
                .map(mapper::toDoctor)
                .toList();
    }

    @Override
    public Optional<Doctor> findByDocumentNumber(String documentNumber) {
        return doctorCrudRepository.findByPersonEntity_DocumentNumberOrderByIdAsc(documentNumber)
                .map(mapper::toDoctor);
    }


    @Override
    public Optional<Doctor> findById(Long id) {
        return doctorCrudRepository.findById(id)
                .map(mapper::toDoctor);
    }

    @Override
    public Optional<Doctor> findByDocumentTypeAndDocumentNumber(DocumentType documentType, String documentNumber) {
        return doctorCrudRepository.findByPersonEntity_DocumentTypeAndPersonEntity_DocumentNumberOrderByIdAsc(documentType, documentNumber)
                .map(mapper::toDoctor);
    }

    @Override
    @Transactional
    public Doctor save(Doctor doctor) {
        DoctorEntity doctorEntity = mapper.toDoctorEntity(doctor);

        PersonEntity savedPersonEntity = personCrudRepository.save(doctorEntity.getPersonEntity());
        doctorEntity.setPersonEntity(savedPersonEntity);

        DoctorEntity savedDoctorEntity = doctorCrudRepository.save(doctorEntity);
        return mapper.toDoctor(savedDoctorEntity);
    }

    @Override
    public void delete(Long id) {
        doctorCrudRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return doctorCrudRepository.existsById(id);
    }

}
