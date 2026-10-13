package com.mrodriguezul.apptapp.domain.model;

import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class Person {

    private Long id;
    private DocumentType documentType;
    private String documentNumber;
    private String names;
    private String surnames;
    private LocalDate dateOfBirth;
    private String email;
    private String phoneNumber;

    public Person(Long id) {
        this.id = id;
    }
}
