package com.higgs.node.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.higgs")
@SuppressWarnings("NonFinalUtilityClass")
public class HomeAssistantNodeServerApplication {
    public static void main(final String[] args) {
        SpringApplication.run(HomeAssistantNodeServerApplication.class, args);
    }
}
