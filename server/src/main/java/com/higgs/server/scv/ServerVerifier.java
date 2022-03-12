package com.higgs.server.scv;

import com.higgs.server.scv.condition.ServerCheck;
import com.higgs.server.scv.condition.SigningKeyCheck;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

/**
 * NOT a spring {@link Component} because it is used outside of spring application context. Uses a Bill Pugh style
 * singleton instance holder.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServerVerifier {
    private final Set<? extends ServerCheck> conditions = Set.of(new SigningKeyCheck());

    /**
     * @param type the check type to filter on and execute
     * @return true if all checks of the given type pass
     */
    public boolean check(final CheckType type) {
        // context object mutable between checks, maintains state
        final VerificationContext verificationContext = this.getVerificationContext(type);
        for (final ServerCheck condition : this.conditions) {
            if (condition.getType() == type && !condition.check(verificationContext)) {
                final Pair<String, Boolean> failureTypeResult = this.fail(verificationContext, condition);
                if (failureTypeResult != null) {
                    ServerVerifier.log.error(failureTypeResult.getLeft());
                    if (Boolean.TRUE.equals(failureTypeResult.getRight())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    VerificationContext getVerificationContext(final CheckType type) {
        return new VerificationContext().setType(type).setSystemProperties(System.getProperties()).setSystemEnv(System.getenv());
    }

    private Pair<String, Boolean> fail(final VerificationContext verificationContext, final ServerCheck condition) {
        final String failureMessage = this.getFailureMessage(verificationContext, condition);
        return switch (condition.getFailureType()) {
            case CHECK_FAILURE_RUNTIME_EXCEPTION -> throw new CheckFailureException(failureMessage);
            case LOG_AND_CONTINUE -> Pair.of(failureMessage, false);
            case LOG_AND_RETURN -> Pair.of(failureMessage, true);
            default -> null;
        };
    }

    String getFailureMessage(final VerificationContext verificationContext, final ServerCheck condition) {
        final String conditionMessage = Optional.ofNullable(verificationContext.getFailureMessages().get(condition)).orElse("[no message]");
        return String.format("%s condition %s failed with message: %s", condition.getType(), condition.getClass().getSimpleName(), conditionMessage);
    }

    private static class ServerVerifierInstanceHolder {
        private static final ServerVerifier INSTANCE = new ServerVerifier();
    }

    public static ServerVerifier getInstance() {
        return ServerVerifierInstanceHolder.INSTANCE;
    }
}
