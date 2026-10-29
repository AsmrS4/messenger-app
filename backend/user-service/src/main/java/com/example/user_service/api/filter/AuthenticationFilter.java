package com.example.user_service.api.filter;

import com.example.user_service.core.http.AuthenticationClient;
import com.example.user_service.core.utils.JwtParser;
import feign.FeignException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationClient authClient;
    private final JwtParser parser;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendGoneResponse(response, "Отсутствует заголовок авторизации");
            return;
        }
        String token = authHeader.substring(7);

        try {
            authClient.checkSession(token);
            UUID userId = parser.getUserId(token);
            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userId,
                        token,
                        null
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (FeignException.Unauthorized e) {
            sendGoneResponse(response, "Получен невалидный токен");
            return;
        } catch (JwtException ex) {
            sendGoneResponse(response, ex.getMessage());
            return;
        }catch (Exception e) {
            log.error("Error while calling auth service", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Сервис не доступен");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void sendGoneResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.format("{\"error\": \"%s\"}", message));
    }
}