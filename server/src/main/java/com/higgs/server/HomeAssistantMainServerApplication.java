package com.higgs.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@SuppressWarnings("NonFinalUtilityClass")
@ComponentScan({ "com.higgs.server", "com.higgs.common" })
@ConfigurationProperties
public class HomeAssistantMainServerApplication {
    public static void main(final String[] args) {
        SpringApplication.run(HomeAssistantMainServerApplication.class, args);
    }
}
