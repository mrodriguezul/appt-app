package com.mrodriguezul.apptapp.persistence.adapter;

import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;
import com.mrodriguezul.apptapp.persistence.crud.PersonCrudRepository;
import com.mrodriguezul.apptapp.persistence.entity.PersonEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PersonRepository {
    private PersonCrudRepository personCrudRepository;

    public PersonRepository(PersonCrudRepository personCrudRepository) {
        this.personCrudRepository = personCrudRepository;
    }
    public List<PersonEntity> findByDocumentNumberOrderByDocumentNumberAsc(String documentNumber) {
        return personCrudRepository.findByDocumentNumberOrderByDocumentNumberAsc(documentNumber);
    }
    public PersonEntity findPersonEntitiesByDocumentTypeAndDocumentNumberOrderByDocumentNumberAsc(DocumentType documentType, String documentNumber) {
        return personCrudRepository.findPersonEntitiesByDocumentTypeAndDocumentNumberOrderByDocumentNumberAsc(documentType, documentNumber)
                .orElse(null);
    }

}
