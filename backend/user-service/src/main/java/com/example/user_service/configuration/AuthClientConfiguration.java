package com.example.user_service.configuration;

import com.example.user_service.core.http.ClientErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthClientConfiguration {
    @Bean
    public ErrorDecoder customErrorDecoder() {
        return new ClientErrorDecoder();
    }
}

