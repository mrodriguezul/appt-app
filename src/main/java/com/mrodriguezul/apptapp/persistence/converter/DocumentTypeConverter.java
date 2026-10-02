package com.mrodriguezul.apptapp.persistence.converter;

import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class DocumentTypeConverter implements AttributeConverter<DocumentType, String> {

    @Override
    public String convertToDatabaseColumn(DocumentType attribute) {
        return attribute != null ? attribute.getCode() : null;
    }

    @Override
    public DocumentType convertToEntityAttribute(String dbData) {
        return dbData != null ? DocumentType.fromCode(dbData) : null;
    }
}
