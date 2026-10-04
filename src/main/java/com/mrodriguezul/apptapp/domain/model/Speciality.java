package com.mrodriguezul.apptapp.domain.model;

public class Speciality {
    private Long id;
    private String name;

    public Speciality(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Speciality() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
