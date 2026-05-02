package kr.sinnau.platform.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ApiResponse<T> {
    private final boolean success;
    private final T data;
    private final ErrorBody error;
    private final LocalDateTime timestamp;

    // 성공 응답 (데이터 포함)
    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // 성공 응답 (데이터 없음)
    public static ApiResponse<?> ok() {
        return ok(null);
    }

    // 실패 응답
    public static ApiResponse<?> fail(String code, String message) {
        return ApiResponse.builder()
                .success(false)
                .error(new ErrorBody(code, message))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Getter
    @AllArgsConstructor
    public static class ErrorBody {
        private final String code;
        private final String message;
    }
}