package com.yan.authentication.web;

import com.yan.authentication.rbac.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class PermissionsController {

    private final PermissionService permissionService;

    @GetMapping("/self/permissions")
    public Map<String, Map<String, Object>> getSelfPermissions(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getPrincipal().toString());
        return permissionService.getEffectivePermissions(userId);
    }
}


