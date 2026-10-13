package com.mrodriguezul.apptapp.persistence.adapter;

import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;
import com.mrodriguezul.apptapp.domain.repository.IPersonRepository;
import com.mrodriguezul.apptapp.persistence.crud.PersonCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PersonRepository implements IPersonRepository {
    private final PersonCrudRepository personCrudRepository;

    @Autowired
    public PersonRepository(PersonCrudRepository personCrudRepository) {
        this.personCrudRepository = personCrudRepository;
    }

    @Override
    public boolean existsByDocumentTypeAndDocumentNumber(DocumentType documentType, String documentNumber) {
        return personCrudRepository.existsByDocumentTypeAndDocumentNumber(documentType, documentNumber);
    }
}
