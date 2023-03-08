package com.amit.gateway.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration to create default users and create rules on which api to authorise and how to handle exceptions.
 */
@Configuration
@EnableWebSecurity
public class BasicConfiguration {

    @Autowired
    @Qualifier("customAuthenticationEntryPoint")
    AuthenticationEntryPoint authEntryPoint;

    /**
     * Creates default users
     */
    @Bean
    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {

        UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder.encode("password"))
                .roles("USER", "ADMIN")
                .build();

        return new InMemoryUserDetailsManager(admin);
    }

    /**
     * Reflects the order on how the authorizations should work
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest()
                .hasRole("ADMIN")
                .and()
                .httpBasic()
                .and().exceptionHandling().authenticationEntryPoint(authEntryPoint);
        return http.build();
    }

    /**
     * Default password encoder to be used
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        return encoder;
    }
}