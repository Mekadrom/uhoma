package com.higgs.simulator.httpsim.db.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Converter(autoApply = true)
public class JpaJsonToMapConverter implements AttributeConverter<Object, String> {
    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(final Object meta) {
        try {
            return JpaJsonToMapConverter.OBJECT_MAPPER.writeValueAsString(meta);
        } catch (final JsonProcessingException e) {
            JpaJsonToMapConverter.log.error("Error converting to json", e);
            return null;
        }
    }

    @Override
    public Object convertToEntityAttribute(final String dbData) {
        try {
            return JpaJsonToMapConverter.OBJECT_MAPPER.readValue(dbData, Map.class);
        } catch (final IOException e) {
            JpaJsonToMapConverter.log.error("Error converting to json", e);
            return null;
        }
    }
}
