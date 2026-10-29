package com.example.authorization_service.core.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtParser {
    @Value("${jwt.secret-key}")
    private String SECRET_KEY;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    public UUID getUserId(String token) {
        return UUID.fromString(getClaims(token).getSubject());
    }

    public boolean isTokenExpired(String token) {
        Date expirationDate = getExpirationDate(token);
        return expirationDate.before(new Date());
    }

    public void validateToken(String token) {
        if (isTokenExpired(token)) throw new JwtException("Время жизни токена истекло, обновите сессию");
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
        } catch (Exception e) {
            throw new JwtException("Полученный токен невалиден");
        }
    }

    public Date getExpirationDate(String token) {
        return getClaims(token).getExpiration();
    }

    public Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception ex) {
            throw new JwtException(ex.getMessage());
        }
    }
}