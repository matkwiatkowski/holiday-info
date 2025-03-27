package com.mkwiatkowski.holidayinfo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Custom filter for authorizing request using API key from X-API-KEY header.
 */
@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "X-API-KEY";
    private static final RequestMatcher IGNORED_PATHS = new AntPathRequestMatcher("/actuator/health/ignore-mail");

    @Value("${holiday-info.security.api-key}")
    private String apiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (IGNORED_PATHS.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        String requestApiKey = request.getHeader(API_KEY_HEADER);
        if (requestApiKey == null || !requestApiKey.equals(apiKey)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API Key");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
