package com.mrodriguezul.apptapp.persistence.audit;

import com.mrodriguezul.apptapp.persistence.entity.PersonEntity;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PreUpdate;

public class AuditPersonListener {

    private PersonEntity currentPersonEntity;

    @PostLoad
    public void postLoad(PersonEntity entity) {
        System.out.println("POST LOAD");
        this.currentPersonEntity = createPersonCopy(entity);
    }

    @PreUpdate
    public void onPreUpdate(PersonEntity entity) {
        System.out.println("PRE UPDATE");
        System.out.println(entity.toString());
    }

    @PostPersist
    @PostUpdate
    public void onPostPersist(PersonEntity entity) {
        System.out.println("POST PERSIST OR UPDATE");
        System.out.println("LAST VALUE: " + (currentPersonEntity != null ? currentPersonEntity.toString() : "null"));
        System.out.println("POST VALUE: " + entity.toString());
    }

    private PersonEntity createPersonCopy(PersonEntity original) {
        if (original == null) return null;

        PersonEntity copy = new PersonEntity();
        copy.setId(original.getId());
        copy.setDocumentType(original.getDocumentType());
        copy.setDocumentNumber(original.getDocumentNumber());
        copy.setNames(original.getNames());
        copy.setSurnames(original.getSurnames());
        copy.setDateOfBirth(original.getDateOfBirth());
        copy.setEmail(original.getEmail());
        copy.setPhoneNumber(original.getPhoneNumber());


        return copy;
    }
}
