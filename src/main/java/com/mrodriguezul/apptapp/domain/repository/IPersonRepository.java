package com.mrodriguezul.apptapp.domain.repository;

import com.mrodriguezul.apptapp.domain.model.Person;
import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;

import java.util.Optional;

public interface IPersonRepository {
   boolean existsByDocumentTypeAndDocumentNumber(DocumentType documentType, String documentNumber);
}
