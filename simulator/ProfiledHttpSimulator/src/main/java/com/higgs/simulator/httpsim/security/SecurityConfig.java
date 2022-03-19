package com.higgs.simulator.httpsim.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    /**
     * This project is not secured, as it is only meant for dev testing and being run locally. It goes without saying,
     * do not store valuable data in this project's database.
     *
     * @param http Security configuration
     * @throws Exception Exception
     */
    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/").permitAll()
                .and()
                .authorizeRequests().antMatchers("/h2-console/**").permitAll()
                .and()
                .headers().frameOptions().sameOrigin()
                .and()
                .cors().disable().csrf().disable();
    }
}
