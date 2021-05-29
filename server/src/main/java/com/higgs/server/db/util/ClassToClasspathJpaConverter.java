package com.higgs.server.db.util;

import lombok.SneakyThrows;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class ClassToClasspathJpaConverter implements AttributeConverter<Class<?>, String> {
    @Override
    public String convertToDatabaseColumn(final Class<?> attribute) {
        return attribute.getName();
    }

    @Override
    @SneakyThrows
    public Class<?> convertToEntityAttribute(final String dbData) {
        return Class.forName(dbData);
    }
}
