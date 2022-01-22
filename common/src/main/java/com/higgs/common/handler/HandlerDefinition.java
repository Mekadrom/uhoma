package com.higgs.common.handler;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class HandlerDefinition {
    private Map<String, Object> metadata;
    private Map<String, Object> def;
}
