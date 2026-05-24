package com.xianyupaidan.security;

public class TenantContext {
    private static final ThreadLocal<String> TENANT_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<Long> USER_ID_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<String> ROLE_HOLDER = new ThreadLocal<>();

    public static void setTenantId(String tenantId) {
        TENANT_HOLDER.set(tenantId);
    }

    public static String getTenantId() {
        return TENANT_HOLDER.get();
    }

    public static void setUserId(Long userId) {
        USER_ID_HOLDER.set(userId);
    }

    public static Long getUserId() {
        return USER_ID_HOLDER.get();
    }

    public static void setRole(String role) {
        ROLE_HOLDER.set(role);
    }

    public static String getRole() {
        return ROLE_HOLDER.get();
    }

    public static void clear() {
        TENANT_HOLDER.remove();
        USER_ID_HOLDER.remove();
        ROLE_HOLDER.remove();
    }
}
