package kr.sinnau.platform.api.local.v1.request;

import lombok.Getter;

@Getter
public class VerifyLoginKeyRequest {
    private String email;
    private String keyString;
}
