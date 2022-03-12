package com.higgs.server.scv.condition;

import com.higgs.server.scv.CheckFailureType;
import com.higgs.server.scv.CheckType;
import com.higgs.server.scv.VerificationContext;
import com.higgs.server.util.ServerUtils;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

public class SigningKeyCheck implements ServerCheck {
    @Override
    public boolean check(final VerificationContext conditionContext) {
        return ServerUtils.getInstance().getSigningKey(conditionContext.getSystemProperties(), conditionContext.getSystemEnv())
                .filter(StringUtils::isNotBlank)
                .isPresent();
    }

    @NonNull
    @Override
    public CheckType getType() {
        return CheckType.PRE_INITIALIZE;
    }

    @NonNull
    @Override
    public CheckFailureType getFailureType() {
        return CheckFailureType.CHECK_FAILURE_RUNTIME_EXCEPTION;
    }
}
