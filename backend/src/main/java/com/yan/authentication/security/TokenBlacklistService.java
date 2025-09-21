package com.yan.authentication.security;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {
    private final Map<String, Long> blacklist = new ConcurrentHashMap<>();

    public void add(String jti, long expiresAtEpoch) {
        blacklist.put(jti, expiresAtEpoch);
    }

    public boolean isBlacklisted(String jti) {
        Long until = blacklist.get(jti);
        if (until == null) return false;
        if (until < Instant.now().getEpochSecond()) {
            blacklist.remove(jti);
            return false;
        }
        return true;
    }
}


