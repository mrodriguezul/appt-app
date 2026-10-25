package com.mrodriguezul.apptapp.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Doctor extends Person {
    private Speciality speciality;
    public Doctor(Long id) {
        super(id);
    }
}
