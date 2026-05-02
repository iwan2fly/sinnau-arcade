package kr.sinnau.platform.common.exception;

import kr.sinnau.platform.common.exception.error.SinnauErrorCode;
import lombok.Getter;

@Getter
public class SinnauException extends RuntimeException {
    private final String code;

    // 1. Enum을 통째로 넘겨서 발생시킬 때 (가장 권장)
    public SinnauException(SinnauErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    // 2. Enum을 쓰지만 메시지만 좀 더 구체적으로 바꾸고 싶을 때
    public SinnauException(SinnauErrorCode errorCode, String detailMessage) {
        super(detailMessage);
        this.code = errorCode.getCode();
    }
}
