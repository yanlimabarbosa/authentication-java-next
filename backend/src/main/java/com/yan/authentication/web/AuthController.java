package com.yan.authentication.web;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.yan.authentication.security.JwtService;
import com.yan.authentication.security.TokenBlacklistService;
import com.yan.authentication.web.dto.LoginRequest;
import com.yan.authentication.web.dto.SignupRequest;
import com.yan.authentication.web.dto.TokensResponse;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;
    private final CookieUtil cookieUtil;
    private final TokenBlacklistService blacklistService;
    private final UserAuthService userAuthService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Validated @RequestBody SignupRequest req) {
        try {
            var user = userAuthService.createUser(req.getEmail(), req.getName(), req.getPassword());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating account: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam(defaultValue = "true") boolean httponly,
                                   @Validated @RequestBody LoginRequest req,
                                   HttpServletResponse resp) {
        var user = userAuthService.authenticate(req.getEmail(), req.getPassword());
        String sub = user.id().toString();

        String access = jwtService.generateAccessToken(sub, java.util.Map.of());
        String refresh = jwtService.generateRefreshToken(sub);

        if (httponly) {
            cookieUtil.setAccessCookie(resp, access, userAuthService.getAccessMinutes());
            cookieUtil.setRefreshCookie(resp, refresh, userAuthService.getRefreshDays());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.ok(new TokensResponse(access, refresh));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(value = "refresh_token_cookie", required = false) String refreshCookie,
                                     @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth,
                                     HttpServletResponse resp) {
        String token = resolveBearerOrCookie(auth, refreshCookie);
        var jws = jwtService.parse(token);
        Claims claims = jws.getBody();
        String sub = claims.getSubject();
        String newAccess = jwtService.generateAccessToken(sub, java.util.Map.of());

        if (auth != null && auth.startsWith("Bearer ")) {
            return ResponseEntity.ok(java.util.Map.of("access_token", newAccess));
        } else {
            cookieUtil.setAccessCookie(resp, newAccess, userAuthService.getAccessMinutes());
            return ResponseEntity.ok().build();
        }
    }

    @DeleteMapping("/revoke-tokens")
    public ResponseEntity<Void> revokeTokens(HttpServletRequest req, HttpServletResponse resp) {
        String token = resolveBearerOrCookie(req.getHeader(HttpHeaders.AUTHORIZATION),
                readCookie(req, "access_token_cookie"));
        if (token != null) {
            var jws = jwtService.parse(token);
            String jti = jws.getBody().getId();
            long exp = jws.getBody().getExpiration().toInstant().getEpochSecond();
            blacklistService.add(jti, exp);
        }
        cookieUtil.clearJwtCookies(resp);
        return ResponseEntity.noContent().build();
    }

    private static String resolveBearerOrCookie(String auth, String cookieValue) {
        if (auth != null && auth.startsWith("Bearer ")) return auth.substring(7);
        return cookieValue;
    }

    private static String readCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        for (var c : request.getCookies()) if (c.getName().equals(name)) return c.getValue();
        return null;
    }
}


