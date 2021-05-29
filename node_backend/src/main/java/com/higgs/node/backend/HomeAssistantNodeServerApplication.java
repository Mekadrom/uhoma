package com.higgs.node.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@SuppressWarnings("NonFinalUtilityClass")
public class HomeAssistantNodeServerApplication {
    public static void main(final String[] args) {
        SpringApplication.run(HomeAssistantNodeServerApplication.class, args);
    }
}
