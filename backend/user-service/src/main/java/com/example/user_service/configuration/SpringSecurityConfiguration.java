package com.example.user_service.configuration;

import com.example.user_service.api.filter.AuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurityConfiguration {
    private final AuthenticationFilter authenticationFilter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .cors(c -> c.configurationSource(
                        request -> {
                            CorsConfiguration corsConfig = new CorsConfiguration();
                            corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
                            corsConfig.setAllowedHeaders(List.of("*"));
                            corsConfig.addAllowedOriginPattern("*");
                            corsConfig.setAllowCredentials(true);

                            return corsConfig;
                        }
                ))
                .authorizeHttpRequests(
                        httpRequest -> httpRequest
                                .requestMatchers(
                                        "/v3/api-docs",
                                        "/v3/api-docs/**",
                                        "/swagger-ui/**",
                                        "/webjars/**",
                                        "/swagger-ui.html",
                                        "/api/users/new",
                                        "/api/users/profile/{userId}"
                                        )
                                .permitAll()
                                .requestMatchers("/error").permitAll()
                                .requestMatchers("/api/users/**", "/api/users/profile/**").authenticated()
                                .anyRequest().permitAll()
                )
                .sessionManagement(c->c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
}
