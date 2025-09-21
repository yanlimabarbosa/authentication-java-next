package com.yan.authentication.rbac.security;

import com.yan.authentication.rbac.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RbacPermissionEvaluator implements PermissionEvaluator {
    private final PermissionService permissionService;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || targetDomainObject == null || permission == null) return false;
        Object principal = authentication.getPrincipal();
        UUID userId;
        try {
            userId = principal instanceof UUID ? (UUID) principal : UUID.fromString(principal.toString());
        } catch (Exception e) {
            return false;
        }
        Map<String, Map<String, Object>> perms = permissionService.getEffectivePermissions(userId);
        Map<String, Object> resource = perms.get(targetDomainObject.toString());
        if (resource == null) return false;
        Object flag = resource.get(permission.toString());
        return Boolean.TRUE.equals(flag) || (flag instanceof Number && ((Number) flag).intValue() == 1);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return hasPermission(authentication, targetType, permission);
    }
}
