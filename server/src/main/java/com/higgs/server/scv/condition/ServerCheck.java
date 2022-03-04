package com.higgs.server.scv.condition;

import com.higgs.server.scv.CheckFailureType;
import com.higgs.server.scv.VerificationContext;
import com.higgs.server.scv.CheckType;
import lombok.NonNull;

public interface ServerCheck {

    boolean check(final VerificationContext conditionContext);

    @NonNull
    CheckType getType();

    @NonNull
    CheckFailureType getFailureType();

}
