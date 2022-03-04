package com.higgs.server.scv;

import com.higgs.server.scv.condition.ServerCheck;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class VerificationContext {
    private CheckType type;
    private Map<ServerCheck, String> failureMessages = new HashMap<>();
}
