package com.mrodriguezul.apptapp.persistence.audit;

import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;
import com.mrodriguezul.apptapp.persistence.entity.PersonEntity;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AuditPersonListenerTest {
    @Test
    void postLoad_shouldKeepIndependentCopyOfPersonFields() throws Exception {
        PersonEntity original = new PersonEntity();
        original.setId(7L);
        original.setDocumentType(DocumentType.DNI);
        original.setDocumentNumber("12345678");
        original.setNames("Ana");
        original.setSurnames("Lopez");
        original.setDateOfBirth(LocalDate.of(1990, 1, 15));
        original.setEmail("ana@example.com");
        original.setPhoneNumber("999999999");
        AuditPersonListener listener = new AuditPersonListener();

        listener.postLoad(original);

        Field field = AuditPersonListener.class.getDeclaredField("currentPersonEntity");
        field.setAccessible(true);
        PersonEntity copy = (PersonEntity) field.get(listener);
        assertNotNull(copy);
        assertNotSame(original, copy);
        assertEquals(original.getId(), copy.getId());
        assertEquals(original.getDocumentType(), copy.getDocumentType());
        assertEquals(original.getDocumentNumber(), copy.getDocumentNumber());
        assertEquals(original.getNames(), copy.getNames());
        assertEquals(original.getSurnames(), copy.getSurnames());
        assertEquals(original.getDateOfBirth(), copy.getDateOfBirth());
        assertEquals(original.getEmail(), copy.getEmail());
        assertEquals(original.getPhoneNumber(), copy.getPhoneNumber());
    }

    @Test
    void postLoad_shouldHandleNullEntity() throws Exception {
        AuditPersonListener listener = new AuditPersonListener();

        listener.postLoad(null);

        Field field = AuditPersonListener.class.getDeclaredField("currentPersonEntity");
        field.setAccessible(true);
        assertNull(field.get(listener));
    }
}
