package com.mrodriguezul.apptapp.persistence.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mrodriguezul.apptapp.persistence.audit.AuditEntity;
import com.mrodriguezul.apptapp.persistence.audit.AuditPersonListener;
import com.mrodriguezul.apptapp.persistence.converter.DocumentTypeSerializer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;
import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "person")
@EntityListeners(AuditPersonListener.class)
public class PersonEntity extends AuditEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "document_type_code", nullable = false, length = 2)
    private DocumentType documentType;

    @Column(name = "document_number", nullable = false, length = 20)
    private String documentNumber;

    @Column(nullable = false, length = 30)
    private String names;

    @Column(length = 40)
    private String surnames;

    @Column(name = "date_of_birth", nullable = false)
    private Date dateOfBirth;

    @Column(length = 30)
    private String email;

    @Column(length = 15)
    private String phoneNumber;

    @OneToOne(mappedBy = "personEntity")
    private DoctorEntity doctorEntity;

    @OneToOne(mappedBy = "personEntity")
    private PatientEntity patientEntity;

    @Override
    public String toString() {
        return "PersonEntity{" +
                "id=" + id +
                ", documentType='" + documentType + '\'' +
                ", documentNumber='" + documentNumber + '\'' +
                ", names='" + names + '\'' +
                ", surnames='" + surnames + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
