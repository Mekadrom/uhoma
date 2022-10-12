package com.higgs.common.kafka;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.SneakyThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class KafkaSerializerProviderTest {
    @SneakyThrows
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "null", "value" })
    void testGetNodeResponseBodySerializer(final String value) {
        final ObjectMapper actual = assertDoesNotThrow(KafkaSerializerProvider::getNodeResponseBodySerializer);
        assertNotNull(actual);

        final JsonSerializer<Object> nullKeySerializer = actual.getSerializerProvider().getDefaultNullKeySerializer();

        try (JsonGenerator generator = mock(JsonGenerator.class)) {
            nullKeySerializer.serialize(value, generator, mock(SerializerProvider.class));
            verify(generator, times(1)).writeFieldName("null");
        }
    }
}
