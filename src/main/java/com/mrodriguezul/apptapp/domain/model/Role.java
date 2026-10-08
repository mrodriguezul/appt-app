package com.mrodriguezul.apptapp.domain.model;

import java.time.LocalDateTime;

public class Role {
    private String name;
    private LocalDateTime grantedDate;

    public Role() {
    }

    public Role(String name, LocalDateTime grantedDate) {
        this.name = name;
        this.grantedDate = grantedDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getGrantedDate() {
        return grantedDate;
    }

    public void setGrantedDate(LocalDateTime grantedDate) {
        this.grantedDate = grantedDate;
    }

    @Override
    public String toString() {
        return "Role{" +
                "name='" + name + '\'' +
                ", grantedDate=" + grantedDate +
                '}';
    }
}
