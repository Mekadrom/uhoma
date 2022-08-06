package com.higgs.common.handler;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HandlerDefinition {
    @JsonProperty("metadata")
    private Map<String, Object> metadata;

    @JsonProperty("def")
    private Map<String, Object> def;
}
