package com.bizuinfo.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/publico/**",
                "/jakarta.faces.resource/**"
                ).permitAll()
                .anyRequest().authenticated()
            ).oauth2Login(oauth -> oauth
             .loginPage("/publico/acesso/login.xhtml")
        );

        return http.build();
    }
}