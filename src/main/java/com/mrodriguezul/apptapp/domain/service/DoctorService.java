package com.mrodriguezul.apptapp.domain.service;

import com.mrodriguezul.apptapp.domain.model.Doctor;
import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;
import com.mrodriguezul.apptapp.domain.repository.IDoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {

    @Autowired
    private IDoctorRepository IDoctorRepository;

    public List<Doctor> getAll() {
        return IDoctorRepository.findAll();
    }

    public List<Doctor> getAllByNameOrSurname(String names, String surnames) {
        return IDoctorRepository.findAllByNameOrSurname(names, surnames);
    }

    public List<Doctor> getAllBySpeciality(Long specialityId) {
        return IDoctorRepository.findAllBySpeciality(specialityId);
    }

    public Optional<Doctor> getAllByDocumentNumber(String documentNumber){
        return IDoctorRepository.findByDocumentNumber(documentNumber);
    }

    public Optional<Doctor> getDoctor(Long id) {
        return IDoctorRepository.findById(id);
    }

    public Optional<Doctor> getDoctorByDocumentTypeAndDocumentNumber(DocumentType documentType, String documentNumber){
        return IDoctorRepository.findByDocumentTypeAndDocumentNumber(documentType, documentNumber);
    }

    public Doctor save(Doctor doctor) {
        return IDoctorRepository.save(doctor);
    }

    public boolean delete(Long id) {
        return getDoctor(id).map(doctor -> {
            IDoctorRepository.delete(id);
            return true;
        }).orElse(false);
    }

    public boolean existsById(Long id) {
        return IDoctorRepository.existsById(id);
    }
}
