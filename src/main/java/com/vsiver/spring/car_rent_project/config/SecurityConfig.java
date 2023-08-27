package com.vsiver.spring.car_rent_project.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity httpSecurity,
            JwtAuthFilter jwtAuthFilter,
            AuthenticationEntryPoint authenticationEntryPoint,
            AuthenticationProvider authenticationProvider
    ) throws Exception {
        httpSecurity
                .csrf()
                    .disable()
                .authorizeHttpRequests()
                    .requestMatchers("/api/v1/public/**")
                        .permitAll()
                    .requestMatchers("/api/v1/user/**")
                        .hasAnyAuthority("USER", "ADMIN")
                    .requestMatchers("/api/v1/admin/**")
                        .hasAuthority("ADMIN")
                    .requestMatchers("/v3/api-docs/**", "/swagger-ui/**")
                        .permitAll()
                    .anyRequest()
                        .authenticated()
                .and()
                    .sessionManagement()
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .exceptionHandling()
                        .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                    .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        httpSecurity.cors();


        return httpSecurity.build();
    }
}
