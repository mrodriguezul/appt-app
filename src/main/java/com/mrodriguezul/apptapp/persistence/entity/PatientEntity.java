package com.mrodriguezul.apptapp.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "patient")
public class PatientEntity implements Serializable {
    @Id
    private Long id;

    @Column(name = "medical_record_number", nullable = false, length = 20)
    private String medicalRecordNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id", nullable = false)
    private PersonEntity personEntity;

    @OneToMany(mappedBy = "patientEntity", fetch = FetchType.LAZY)
    @OrderBy("appointmentDate DESC")
    private List<AppointmentEntity> appointments;
}
