package com.mrodriguezul.apptapp.persistence.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;

import java.io.IOException;

public class DocumentTypeSerializer extends JsonSerializer<DocumentType> {
    @Override
    public void serialize(DocumentType documentType, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(documentType.getCode());
    }
}
