package com.yan.authentication.web;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

@Component
public class CookieUtil {

    @Value("${app.cookies.secure:true}")
    private boolean secure;

    @Value("${app.cookies.sameSite:None}")
    private String sameSite;

    public void setAccessCookie(HttpServletResponse resp, String jwt, long minutes) {
        ResponseCookie access = ResponseCookie.from("access_token_cookie", jwt)
                .httpOnly(true).secure(secure).sameSite(sameSite)
                .path("/")
                .maxAge(Duration.ofMinutes(minutes))
                .build();
        resp.addHeader(HttpHeaders.SET_COOKIE, access.toString());

        String csrf = UUID.randomUUID().toString();
        ResponseCookie csrfC = ResponseCookie.from("csrf_access_token", csrf)
                .httpOnly(false).secure(secure).sameSite(sameSite)
                .path("/")
                .maxAge(Duration.ofMinutes(minutes))
                .build();
        resp.addHeader(HttpHeaders.SET_COOKIE, csrfC.toString());
        resp.setHeader("X-CSRF-Token", csrf);
    }

    public void setRefreshCookie(HttpServletResponse resp, String jwt, long days) {
        ResponseCookie refresh = ResponseCookie.from("refresh_token_cookie", jwt)
                .httpOnly(true).secure(secure).sameSite(sameSite)
                .path("/api/auth/refresh")
                .maxAge(Duration.ofDays(days))
                .build();
        resp.addHeader(HttpHeaders.SET_COOKIE, refresh.toString());
    }

    public void clearJwtCookies(HttpServletResponse resp) {
        resp.addHeader(HttpHeaders.SET_COOKIE, ResponseCookie.from("access_token_cookie","")
                .httpOnly(true).secure(secure).sameSite(sameSite).path("/").maxAge(0).build().toString());
        resp.addHeader(HttpHeaders.SET_COOKIE, ResponseCookie.from("refresh_token_cookie","")
                .httpOnly(true).secure(secure).sameSite(sameSite).path("/api/auth/refresh").maxAge(0).build().toString());
        resp.addHeader(HttpHeaders.SET_COOKIE, ResponseCookie.from("csrf_access_token","")
                .httpOnly(false).secure(secure).sameSite(sameSite).path("/").maxAge(0).build().toString());
    }
}


