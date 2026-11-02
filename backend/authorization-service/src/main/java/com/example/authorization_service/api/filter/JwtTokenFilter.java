package com.example.authorization_service.api.filter;

import com.example.authorization_service.core.utils.JwtParser;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    private final static String AUTHORIZATION_HEADER = "Authorization";
    private final static String BEARER_PREFIX = "Bearer ";
    private final static int START_TOKEN_INDEX = BEARER_PREFIX.length();

    private final JwtParser parser;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/webjars")
                || path.equals("/swagger-ui.html")
                || (request.getMethod().equals("POST") && path.equals("/api/users/new"))
                || (request.getMethod().equals("GET") && path.equals("/api/users/profile/{userId}"))
                || path.startsWith("/api/auth/login")
                || path.startsWith("/api/auth/register");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if(authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            String accessToken = authHeader.substring(START_TOKEN_INDEX);
            UUID userId = null;
            try {
                parser.validateToken(accessToken);
                userId = parser.getUserId(accessToken);
            } catch (Exception ex) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                String jsonError = String.format("{\"error\": \"Unauthorized\", \"message\": \"%s\"}", ex.getMessage());
                response.getWriter().write(jsonError);
                return;
            }

            if(userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                SecurityContext context = SecurityContextHolder.createEmptyContext();

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userId, accessToken, null);
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                context.setAuthentication(authenticationToken);
                SecurityContextHolder.setContext(context);
            }
        }
        filterChain.doFilter(request, response);
    }
}
