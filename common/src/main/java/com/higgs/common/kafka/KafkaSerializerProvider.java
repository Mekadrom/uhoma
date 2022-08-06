package com.higgs.common.kafka;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.NoArgsConstructor;

import java.io.IOException;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class KafkaSerializerProvider {
    public static ObjectMapper getNodeResponseBodySerializer() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.getSerializerProvider().setNullKeySerializer(new JsonSerializer<>() {
            @Override
            public void serialize(final Object value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
                gen.writeFieldName("null");
            }
        });
        return mapper;
    }
}
