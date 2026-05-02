package kr.sinnau.platform.common.exception.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SinnauErrorCode {

    // 1. 공통/인프라 에러 (Common)
    INVALID_INPUT("C001", "입력값이 올바르지 않습니다."),
    METHOD_NOT_ALLOWED("C002", "지원하지 않는 HTTP 메서드입니다."),
    TYPE_MISMATCH("C003", "파라미터 타입이 잘못되었습니다."),
    SERVER_ERROR("C004", "서버에서 알 수 없는 오류가 발생했습니다."),

    // 2. 인증/인가 (Auth/User) - 예시
    UNAUTHORIZED("A001", "로그인이 필요합니다."),
    ACCESS_DENIED("A002", "접근 권한이 없습니다."),
    USER_NOT_FOUND("U001", "사용자를 찾을 수 없습니다."),
    DUPLICATE_EMAIL("U002", "이미 사용 중인 이메일입니다."),

    // 3. 비즈니스 로직 에러 - 예시
    ORDER_NOT_FOUND("O001", "주문 내역을 찾을 수 없습니다."),
    OUT_OF_STOCK("O002", "재고가 부족합니다.");

    private final String code;
    private final String message;

}
