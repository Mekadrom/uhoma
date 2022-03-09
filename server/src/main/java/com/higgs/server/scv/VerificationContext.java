package com.higgs.server.scv;

import com.higgs.server.scv.condition.ServerCheck;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class VerificationContext {
    private CheckType type;
    private Map<ServerCheck, String> failureMessages = new HashMap<>();
    private Map<String, String> systemEnv = new HashMap<>();
    private Map<Object, Object> systemProperties = new HashMap<>();
}
