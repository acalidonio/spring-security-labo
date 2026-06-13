package com.server.app.config;

import org.springframework.util.AntPathMatcher;

import java.util.Map;
import java.util.Set;

public class SecurityRules {

    public static final Map<String, Set<String>> PUBLIC = Map.of(
            "GET", Set.of("/api/public/info"),
            "POST", Set.of("/api/auth/login","/api/auth/signup")
    );

    public static final Map<String, Set<String>> AUTH_ONLY = Map.of(
            "GET", Set.of("/api/auth/profile", "/api/finanzas/portafolios", "/api/finanzas/activos", "/api/finanzas/portafolios/{id}/rendimiento"),
            "POST", Set.of("/api/auth/logout", "/api/finanzas/portafolios", "/api/finanzas/inversiones"),
            "PUT", Set.of("/api/auth/update/profile", "/api/auth/update/password")
    );

    public static final Set<String> IGNORED = Set.of("/error");

    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    public static boolean isPublic(String method, String path) {
        if (!PUBLIC.containsKey(method)) return false;
        return PUBLIC.get(method).stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    public static boolean isAuthOnly(String method, String path) {
        if (!AUTH_ONLY.containsKey(method)) return false;
        return AUTH_ONLY.get(method).stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    public static boolean isIgnored(String path) {
        return IGNORED.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    public static boolean requiresAuth(String method, String path) {
        return !isPublic(method, path) && !isIgnored(path);
    }
}