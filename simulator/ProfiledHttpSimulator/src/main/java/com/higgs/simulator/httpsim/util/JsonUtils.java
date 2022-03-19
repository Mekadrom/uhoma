package com.higgs.simulator.httpsim.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JsonUtils {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String convertMapToJsonString(final Map<String, Object> map) {
        try {
            return JsonUtils.OBJECT_MAPPER.writeValueAsString(map);
        } catch (final JsonProcessingException e) {
            JsonUtils.log.error("Error converting to json", e);
            return null;
        }
    }

    public static Map<String, Object> convertJsonStringToMappedValues(final String json) {
        try {
            return JsonUtils.OBJECT_MAPPER.readValue(json, new TypeReference<>() {});
        } catch (final IOException e) {
            JsonUtils.log.error("Error converting to json", e);
            return Map.of();
        }
    }

    public static String serialize(final Object object) {
        try {
            return JsonUtils.OBJECT_MAPPER.writeValueAsString(object);
        } catch (final JsonProcessingException e) {
            JsonUtils.log.error("Error serializing object", e);
            return null;
        }
    }
}
