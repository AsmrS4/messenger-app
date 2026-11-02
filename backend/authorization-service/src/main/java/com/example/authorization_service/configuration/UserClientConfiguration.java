package com.example.authorization_service.configuration;


import com.example.authorization_service.core.http.ClientErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserClientConfiguration {

    @Bean
    public ErrorDecoder customErrorDecoder() {
        return new ClientErrorDecoder();
    }
}
