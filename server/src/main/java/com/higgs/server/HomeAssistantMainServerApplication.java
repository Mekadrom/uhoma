package com.higgs.server;

import com.higgs.server.scv.CheckFailureException;
import com.higgs.server.scv.CheckType;
import com.higgs.server.scv.ServerVerifier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ConfigurationProperties
@SuppressWarnings("NonFinalUtilityClass")
@ComponentScan({ "com.higgs.server", "com.higgs.common" })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HomeAssistantMainServerApplication {
    public static void main(final String... args) {
        HomeAssistantMainServerApplication.init(ServerVerifier.getInstance(),
                () -> SpringApplication.run(HomeAssistantMainServerApplication.class, args), args);
    }

    static void init(final ServerVerifier serverVerifier, final Runnable serverInit, final String... args) {
        HomeAssistantMainServerApplication.loadProperties(args);
        HomeAssistantMainServerApplication.check(serverVerifier, CheckType.PRE_INITIALIZE);
        serverInit.run();
        HomeAssistantMainServerApplication.check(serverVerifier, CheckType.POST_INITIALIZE);
    }

    static void check(final ServerVerifier serverVerifier, final CheckType checkType) {
        if (!serverVerifier.check(checkType)) {
            throw new CheckFailureException(String.format("System exited with exit code %s", checkType.getExitCode()));
        }
    }

    static void loadProperties(final String... args) {
        for (final String arg : args) {
            if (arg.contains("=")) {
                final String[] prop = arg.split("=");
                System.setProperty(prop[0].substring(arg.startsWith("--") ? 2 : 0), prop[1]);
            } else {
                System.setProperty(arg.replaceAll("--", ""), Boolean.TRUE.toString());
            }
        }
    }
}
