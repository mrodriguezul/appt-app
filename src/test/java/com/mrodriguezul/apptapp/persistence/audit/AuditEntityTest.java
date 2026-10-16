package com.mrodriguezul.apptapp.persistence.audit;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.junit.jupiter.api.Test;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AuditEntityTest {
    @Test
    void shouldStoreAuditProperties() {
        AuditEntity entity = new AuditEntity();
        LocalDateTime created = LocalDateTime.of(2026, 1, 2, 3, 4);
        LocalDateTime modified = LocalDateTime.of(2026, 2, 3, 4, 5);

        entity.setCreatedDate(created);
        entity.setModifiedDate(modified);
        entity.setCreatedBy("alice");
        entity.setModifiedBy("bob");

        assertEquals(created, entity.getCreatedDate());
        assertEquals(modified, entity.getModifiedDate());
        assertEquals("alice", entity.getCreatedBy());
        assertEquals("bob", entity.getModifiedBy());
    }

    @Test
    void shouldDeclareAuditingMetadata() throws NoSuchFieldException {
        assertTrue(AuditEntity.class.isAnnotationPresent(MappedSuperclass.class));
        assertArrayEquals(new Class<?>[]{AuditingEntityListener.class},
                AuditEntity.class.getAnnotation(EntityListeners.class).value());

        assertNotNull(AuditEntity.class.getDeclaredField("createdDate").getAnnotation(CreatedDate.class));
        assertNotNull(AuditEntity.class.getDeclaredField("modifiedDate").getAnnotation(LastModifiedDate.class));
        assertNotNull(AuditEntity.class.getDeclaredField("createdBy").getAnnotation(CreatedBy.class));
        assertNotNull(AuditEntity.class.getDeclaredField("modifiedBy").getAnnotation(LastModifiedBy.class));
        assertFalse(AuditEntity.class.getDeclaredField("createdDate").getAnnotation(Column.class).updatable());
        assertFalse(AuditEntity.class.getDeclaredField("createdBy").getAnnotation(Column.class).updatable());
    }
}
