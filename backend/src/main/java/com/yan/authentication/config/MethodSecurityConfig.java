package com.yan.authentication.config;

import com.yan.authentication.rbac.security.RbacPermissionEvaluator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig {
    @Bean
    public PermissionEvaluator permissionEvaluator(RbacPermissionEvaluator evaluator) {
        return evaluator;
    }
}
