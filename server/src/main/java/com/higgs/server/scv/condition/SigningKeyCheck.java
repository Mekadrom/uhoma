package com.higgs.server.scv.condition;

import com.higgs.server.scv.CheckFailureType;
import com.higgs.server.scv.CheckType;
import com.higgs.server.scv.VerificationContext;
import com.higgs.server.util.ServerUtils;
import lombok.NonNull;

public class SigningKeyCheck implements ServerCheck {
    @Override
    public boolean check(final VerificationContext conditionContext) {
        return ServerUtils.getSigningKey(conditionContext.getSystemProperties(), conditionContext.getSystemEnv()).isPresent();
    }

    @NonNull
    @Override
    public CheckType getType() {
        return CheckType.PRE_INITIALIZE;
    }

    @NonNull
    @Override
    public CheckFailureType getFailureType() {
        return CheckFailureType.RUNTIME_EXCEPTION;
    }
}
