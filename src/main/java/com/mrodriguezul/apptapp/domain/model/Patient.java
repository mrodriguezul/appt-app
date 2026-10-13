package com.mrodriguezul.apptapp.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Patient extends Person {
    private String medicalRecordNumber;

    public Patient(Long id) {
        super(id);
    }
}
