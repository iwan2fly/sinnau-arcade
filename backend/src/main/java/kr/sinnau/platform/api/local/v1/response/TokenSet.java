package kr.sinnau.platform.api.local.v1.response;

public record TokenSet(
        String accessToken,
        String refreshToken
) {}