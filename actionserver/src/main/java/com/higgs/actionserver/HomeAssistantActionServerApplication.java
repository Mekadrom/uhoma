package com.higgs.actionserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@SuppressWarnings("NonFinalUtilityClass")
@ComponentScan({ "com.higgs.actionserver", "com.higgs.common" })
public class HomeAssistantActionServerApplication {
    public static void main(final String... args) {
        SpringApplication.run(HomeAssistantActionServerApplication.class, args);
    }
}
