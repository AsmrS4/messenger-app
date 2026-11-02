package com.example.authorization_service.core.http;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;

@Slf4j
public class ClientErrorDecoder implements ErrorDecoder {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        String message = extractMessage(response);
        log.error(response.toString());
        return switch (response.status()) {
            case 400 -> new IllegalArgumentException(message);
            case 401 -> new AuthenticationServiceException(message);
            case 404 -> new NoSuchElementException(message);
            default -> new RuntimeException(message);
        };
    }

    private String extractMessage(Response response) {
        if (response.body() == null) {
            return "Unknown error";
        }
        try (InputStreamReader reader = new InputStreamReader(response.body().asInputStream(), StandardCharsets.UTF_8)) {
            JsonNode node = objectMapper.readTree(reader);
            if (node.has("message")) {
                return node.get("message").asText();
            }
            if (node.has("error")) {
                return node.get("error").asText();
            }
        } catch (IOException e) {
            return "Unknown error";
        }
        return "Unknown error";
    }
}
