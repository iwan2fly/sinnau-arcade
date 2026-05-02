package kr.sinnau.platform.config;

public abstract class SecurityConstants{

    public static final String[] AUTH_WHITELIST = {
            "/health",
            "/api/local/v1/auth/**",
            "/", "/index",
            "/gate/**",
            "/css/**", "/js/**", "/images/**"
    };
}
