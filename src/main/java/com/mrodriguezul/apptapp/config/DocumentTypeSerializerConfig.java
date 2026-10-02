package com.mrodriguezul.apptapp.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class DocumentTypeSerializerConfig {
    // De Objeto Java (Enum) a JSON
    public static class Serializer extends JsonSerializer<DocumentType> {
        @Override
        public void serialize(DocumentType documentType, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeString(documentType.getCode()); // Escribe "01"
        }
    }

    // De JSON a Objeto Java (Enum)
    public static class Deserializer extends JsonDeserializer<DocumentType> {
        @Override
        public DocumentType deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            String code = jsonParser.getValueAsString();
            return DocumentType.fromCode(code); // Convierte "01" a DocumentType.DNI
        }
    }
}
