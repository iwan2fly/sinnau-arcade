package kr.sinnau.platform.api.local.v1.response;

public record LoginResponse(String grantType, String accessToken,
                            String refreshToken,   // 추가
                            long expiresIn) {}        // 만료 시간(초)) {}

