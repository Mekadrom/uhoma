package com.higgs.server.web.svc.util.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class DtoEntityMapper {
    public <S, T> T map(final S source, final Class<T> targetClass) {
        return this.getObjectMapper().convertValue(source, targetClass);
    }

    protected ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }
}
