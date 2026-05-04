package kr.sinnau.platform.api.local.v1.response;

public record ProfileResponse(
        String nickName,
        String coin,
        boolean needsNicknameSetup
) {}
