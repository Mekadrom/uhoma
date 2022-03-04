package com.higgs.server.scv;

import com.higgs.server.scv.condition.ServerCheck;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.reflections.Reflections;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * NOT a spring {@link Component} because it is used outside of spring application context. Uses a Bill Pugh style
 * singleton
 */
@Slf4j
public class ServerVerifier {
    private final Set<? extends ServerCheck> conditions;

    public ServerVerifier() {
        this.conditions = this.reflectionInitConditions();
    }

    private Set<? extends ServerCheck> reflectionInitConditions() {
        final Reflections reflections = new Reflections("com.higgs.server.scv.condition");
        final Set<Class<? extends ServerCheck>> set = reflections.getSubTypesOf(ServerCheck.class);
        return set.stream().map(aClass -> {
            try {
                return aClass.getConstructor().newInstance();
            } catch (final ReflectiveOperationException e) {
                ServerVerifier.log.error(String.format("failure instantiating ServerCheck rule %s", aClass), e);
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public boolean check(final CheckType type) {
        // context object mutable between checks, maintains state
        final VerificationContext verificationContext = new VerificationContext().setType(type);
        for (final ServerCheck condition : this.conditions) {
            if (condition.getType() == type) {
                if (!condition.check(verificationContext)) {
                    final Pair<String, Boolean> failureTypeResult = this.fail(verificationContext, condition);
                    if (StringUtils.isNotBlank(failureTypeResult.getLeft())) {
                        ServerVerifier.log.error(failureTypeResult.getLeft());
                        if (Boolean.TRUE.equals(failureTypeResult.getRight())) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private Pair<String, Boolean> fail(final VerificationContext verificationContext, final ServerCheck condition) {
        final String failureMessage = this.getFailureMessage(verificationContext, condition);
        return switch (condition.getFailureType()) {
            case RUNTIME_EXCEPTION -> throw new RuntimeException(failureMessage);
            case LOG_AND_CONTINUE -> Pair.of(failureMessage, false);
            case LOG_AND_RETURN -> Pair.of(failureMessage, true);
        };
    }

    private String getFailureMessage(final VerificationContext verificationContext, final ServerCheck condition) {
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

