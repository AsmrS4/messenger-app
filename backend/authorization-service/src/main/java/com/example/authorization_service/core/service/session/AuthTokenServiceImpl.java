package com.example.authorization_service.core.service.session;

import com.example.authorization_service.core.persistence.AccessTokenRepository;
import com.example.authorization_service.core.utils.JwtParser;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthTokenServiceImpl implements AccessTokenService {

    @Value("${jwt.life-time}")
    private Duration ACCESS_LIFE_TIME;

    private final AccessTokenRepository accessTokenRepository;
    private final JwtParser parser;

    private String generateToken(UUID userId) {
        Date issuedAt = new Date();
        Date expiredAt = new Date(issuedAt.getTime() + ACCESS_LIFE_TIME.toMillis());

        return Jwts.builder()
                .issuedAt(issuedAt)
                .expiration(expiredAt)
                .subject(String.valueOf(userId))
                .signWith(parser.getSigningKey())
                .compact();
    }

    @Override
    public String createAccessToken(UUID userId) {
        return generateToken(userId);
    }

    @Override
    public void validateAccessToken(String token) {
        if (accessTokenRepository.isBlacklisted(token)) {
            throw new JwtException("Полученный токен был ранее отозван.");
        }
        parser.validateToken(token);
    }

    @Override
    public void invalidateAccessToken(String token) {
        validateAccessToken(token);
        if (!accessTokenRepository.isBlacklisted(token)) {
            accessTokenRepository.addToBlacklist(token);
        }
    }
}