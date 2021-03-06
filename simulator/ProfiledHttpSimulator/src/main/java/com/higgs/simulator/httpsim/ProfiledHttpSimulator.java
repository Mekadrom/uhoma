package com.higgs.simulator.httpsim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ConfigurationProperties
@SuppressWarnings("NonFinalUtilityClass")
@ComponentScan({ "com.higgs.simulator.httpsim" })
public class ProfiledHttpSimulator {
    public static void main(final String[] args) {
        SpringApplication.run(ProfiledHttpSimulator.class, args);
    }
}
