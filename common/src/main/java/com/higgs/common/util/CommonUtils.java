package com.higgs.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CommonUtils {
    @Getter
    @NonNull
    private final ObjectMapper defaultMapper = new ObjectMapper();

    public Map<String, Object> parseMap(final String jsonBody) throws JsonProcessingException {
        return this.defaultMapper.readValue(jsonBody, new TypeReference<>() {});
    }

    public <V> Map<String, Object> toObjectMap(@NonNull final Map<String, V> map) {
        return map.entrySet().stream()
                .map(it -> new AbstractMap.SimpleEntry<>(it.getKey(), it.getValue()))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    }

    public <K, V> Map<K, V> flattenMap(final Map<K, List<V>> map) {
        return map.entrySet().stream()
                .map(it -> new AbstractMap.SimpleEntry<>(it.getKey(), it.getValue().stream()
                        .filter(Objects::nonNull)
                        .findFirst()
                        .orElse(null)))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    }

    public String getStringValue(final Object value) {
        return value == null ? null : value.toString();
    }

    public boolean getBooleanValue(final String value, final boolean defaultValue) {
        return StringUtils.isNotBlank(value) ? Boolean.parseBoolean(value) : defaultValue;
    }
}
