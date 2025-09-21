package com.yan.authentication.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final TokenBlacklistService blacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String token = resolveAccessToken(request);
            if (token != null) {
                var jws = jwtService.parse(token);
                Claims claims = jws.getBody();

                String jti = claims.getId();
                if (jti != null && blacklistService.isBlacklisted(jti)) {
                    filterChain.doFilter(request, response);
                    return;
                }

                if (isFromCookie(request)) {
                    String headerCsrf = request.getHeader("X-CSRF-Token");
                    String cookieCsrf = readCookie(request, "csrf_access_token").orElse(null);
                    if (cookieCsrf == null || !cookieCsrf.equals(headerCsrf)) {
                        filterChain.doFilter(request, response);
                        return;
                    }
                }

                String userId = claims.getSubject();
                AbstractAuthenticationToken auth = new AbstractAuthenticationToken(AuthorityUtils.NO_AUTHORITIES) {
                    @Override public Object getCredentials() { return token; }
                    @Override public Object getPrincipal() { return userId; }
                };
                auth.setAuthenticated(true);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception ignored) {
        }

        filterChain.doFilter(request, response);
    }

    private boolean isFromCookie(HttpServletRequest request) {
        return readCookie(request, "access_token_cookie").isPresent();
    }

    private String resolveAccessToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return readCookie(request, "access_token_cookie").orElse(null);
    }

    private Optional<String> readCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return Optional.empty();
        return Arrays.stream(cookies)
                .filter(c -> c.getName().equals(name))
                .map(Cookie::getValue)
                .findFirst();
    }
}


