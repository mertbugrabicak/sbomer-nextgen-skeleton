package org.jboss.sbomer.generator.adapter.out.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@Converter
@Slf4j
public class MapToStringConverter implements AttributeConverter<Map<String, String>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Converts the Map into a JSON string to be stored in the database.
     */
    @Override
    public String convertToDatabaseColumn(Map<String, String> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            log.error("Could not convert map to JSON string", e);
            throw new IllegalArgumentException("Error converting map to JSON", e);
        }
    }

    /**
     * Converts the JSON string from the database back into a Map.
     */
    @Override
    public Map<String, String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(dbData, Map.class);
        } catch (IOException e) {
            log.error("Could not convert JSON string to map", e);
            throw new IllegalArgumentException("Error converting JSON to map", e);
        }
    }
}
