package com.example.user_service.configuration;

import com.example.user_service.core.http.AuthValidationInterceptor;
import com.example.user_service.core.http.AuthenticationClient;
import com.example.user_service.core.http.ClientErrorDecoder;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthClientConfiguration {

    @Bean
    public RequestInterceptor authValidationInterceptor(AuthenticationClient authServiceClient) {
        return new AuthValidationInterceptor(authServiceClient);
    }

    @Bean
    public ErrorDecoder customErrorDecoder() {
        return new ClientErrorDecoder();
    }
}

