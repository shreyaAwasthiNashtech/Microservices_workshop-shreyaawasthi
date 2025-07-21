package com.estore.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/admin/**").hasRole("ADMIN")
                .anyExchange().permitAll()
            )
            .httpBasic(Customizer.withDefaults())
            .csrf(ServerHttpSecurity.CsrfSpec::disable);

        return http.build();
    }
}
