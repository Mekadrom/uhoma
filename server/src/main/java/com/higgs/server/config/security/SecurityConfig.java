package com.higgs.server.config.security;

import com.higgs.server.db.repo.UserLoginRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

@Profile({"prod"})
@EnableWebSecurity
@AllArgsConstructor
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserLoginRepository userLoginRepository;
    private final JwtTokenFilter jwtTokenFilter;

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(username -> this.userLoginRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User: %s not found.", username))));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http = http.exceptionHandling()
                .authenticationEntryPoint((request, response, e) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage()))
                .and();

        http.authorizeRequests()
                .antMatchers("/auth/login").permitAll()
                .antMatchers("/auth/refreshToken").permitAll()
                .antMatchers("/auth/refreshUserView").authenticated()
                .antMatchers("/actionHandler").authenticated()
                .antMatchers("/actionParameterType").authenticated()
                .antMatchers("/metadata").authenticated()
                .antMatchers("/node").authenticated()
                .antMatchers("/room").authenticated();

        http.cors();
        http.csrf().disable();

        http.addFilterBefore(this.jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
