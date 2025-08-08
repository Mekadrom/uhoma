package com.higgs.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan("com.higgs.security")
@EntityScan("com.higgs.security.entity")
@EnableJpaRepositories("com.higgs.security.repo")
public class HomeAssistantSecurityServer {
    public static void main(final String[] args) {
        SpringApplication.run(HomeAssistantSecurityServer.class);
    }
}
