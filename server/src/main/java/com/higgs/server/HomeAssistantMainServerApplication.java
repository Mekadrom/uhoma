package com.higgs.server;

import com.higgs.server.scv.CheckType;
import com.higgs.server.scv.ServerVerifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ConfigurationProperties
@SuppressWarnings("NonFinalUtilityClass")
@ComponentScan({ "com.higgs.server", "com.higgs.common" })
public class HomeAssistantMainServerApplication {
    public static void main(final String... args) {
        HomeAssistantMainServerApplication.loadProperties(args);
        final ServerVerifier serverVerifier = ServerVerifier.getInstance();
        if (!serverVerifier.check(CheckType.PRE_INITIALIZE)) {
            System.exit(10);
        }
        SpringApplication.run(HomeAssistantMainServerApplication.class, args);
        if (!serverVerifier.check(CheckType.POST_INITIALIZE)) {
            System.exit(20);
        }
    }

    private static void loadProperties(final String... args) {
        for (final String arg : args) {
            if (arg.contains("=")) {
                final String[] prop = arg.split("=");
                System.setProperty(prop[0].substring(arg.startsWith("--") ? 2 : 0), prop[1]);
            } else {
                System.setProperty(arg, Boolean.TRUE.toString());
            }
        }
    }
}

