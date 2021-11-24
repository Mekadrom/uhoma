package com.higgs.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class CommonUtil {
    @Getter
    private final ObjectMapper defaultMapper = new ObjectMapper();

    public Map<String, Object> parseMap(final String jsonBody) throws JsonProcessingException {
        final TypeReference<HashMap<String, Object>> typeRef = new TypeReference<>() {};
        return this.defaultMapper.readValue(jsonBody, typeRef);
    }

    public <V> Map<String, Object> toObjectMap(@NonNull final Map<String, V> map) {
        return map.entrySet().stream()
                .map(it -> new AbstractMap.SimpleEntry<>(it.getKey(), (Object) it.getValue()))
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
}
