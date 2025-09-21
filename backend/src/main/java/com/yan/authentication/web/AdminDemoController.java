package com.yan.authentication.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/export")
public class AdminDemoController {

    @GetMapping("/leads")
    @PreAuthorize("hasPermission('usuario','exportar')")
    public Map<String, Object> exportLeads() {
        return Map.of("status", "ok", "message", "export simulated");
    }
}


