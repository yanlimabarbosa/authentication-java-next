package com.yan.authentication.rbac.service;

import com.yan.authentication.rbac.domain.PermissionMenu;
import com.yan.authentication.rbac.domain.User;
import com.yan.authentication.rbac.repo.PermissionMenuRepository;
import com.yan.authentication.rbac.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final UserRepository userRepository;
    private final PermissionMenuRepository permissionMenuRepository;

    public Map<String, Map<String, Object>> getEffectivePermissions(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow();
        List<PermissionMenu> rows = permissionMenuRepository.findByProfileIn(user.getProfiles());

        Map<String, Map<String, Object>> result = new HashMap<>();
        for (PermissionMenu row : rows) {
            String resource = row.getMenu().getKey();
            Map<String, Object> current = result.computeIfAbsent(resource, k -> new HashMap<>());
            current.put("acessar", orTrue(current.get("acessar"), row.getAcessar()));
            current.put("criar", orTrue(current.get("criar"), row.getCriar()));
            current.put("editar", orTrue(current.get("editar"), row.getEditar()));
            current.put("deletar", orTrue(current.get("deletar"), row.getDeletar()));
            current.put("exportar", orTrue(current.get("exportar"), row.getExportar()));
        }
        return result;
    }

    private static Object orTrue(Object a, Boolean b) {
        boolean left = a instanceof Boolean ? (Boolean) a : false;
        boolean right = b != null && b;
        return left || right;
    }
}


