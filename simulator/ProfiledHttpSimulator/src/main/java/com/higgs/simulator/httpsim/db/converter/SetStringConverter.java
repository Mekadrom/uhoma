package com.higgs.simulator.httpsim.db.converter;

import com.higgs.simulator.httpsim.util.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Optional;
import java.util.Set;

@Converter(autoApply = true)
public class SetStringConverter implements AttributeConverter<Set<String>, String> {
    @Override
    public String convertToDatabaseColumn(final Set<String> attribute) {
        return Optional.ofNullable(StringUtils.setToString(attribute))
                .map(it -> it.replaceAll("\\s*", ""))
                .orElse("");
    }

    @Override
    public Set<String> convertToEntityAttribute(final String dbData) {
        return StringUtils.stringToSet(dbData);
    }
}
