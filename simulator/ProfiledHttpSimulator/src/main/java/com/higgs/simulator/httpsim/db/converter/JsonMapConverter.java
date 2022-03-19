package com.higgs.simulator.httpsim.db.converter;

import com.higgs.simulator.httpsim.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Converter(autoApply = true)
public class JsonMapConverter implements AttributeConverter<Map<String, Object>, String> {
    @Override
    public String convertToDatabaseColumn(final Map<String, Object> attribute) {
        return Optional.ofNullable(JsonUtils.convertMapToJsonString(attribute))
                .map(it -> it.replaceAll("\\s*", ""))
                .orElse("");
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(final String dbData) {
        return JsonUtils.convertJsonStringToMappedValues(dbData);
    }
}
