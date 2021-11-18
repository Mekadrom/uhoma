package com.higgs.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommonUtil {
    @Getter
    private final ObjectMapper defaultMapper = new ObjectMapper();

    public Map<String, Object> parseMap(final String jsonBody) throws JsonProcessingException {
        final TypeReference<HashMap<String, Object>> typeRef = new TypeReference<>() {};
        return this.defaultMapper.readValue(jsonBody, typeRef);
    }

    public Map<String, Object> flatMap(final Map<String, List<Object>> headers) {
        return headers.entrySet().stream()
                .map(it -> new AbstractMap.SimpleEntry<>(it.getKey(), it.getValue().stream()
                        .findFirst()
                        .orElse(null)))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    }
}
