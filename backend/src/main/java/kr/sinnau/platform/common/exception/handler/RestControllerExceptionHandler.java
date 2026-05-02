package kr.sinnau.platform.common.exception.handler;

import kr.sinnau.platform.common.dto.ApiResponse;
import kr.sinnau.platform.common.exception.SinnauException;
import kr.sinnau.platform.common.exception.error.SinnauErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@Order(1) // API 예외를 최우선으로 잡습니다.
@RestControllerAdvice(annotations = RestController.class)
public class RestControllerExceptionHandler {

    /**
     * 1. @Valid 또는 @Validated로 데이터 검증 실패 시 발생 (400 Bad Request)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleValidationException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        log.warn("[Validation Error] : {}", errorMessage);
        // Enum에서 기본 코드인 C001(INVALID_INPUT)을 가져다 씁니다.
        return ApiResponse.fail(SinnauErrorCode.INVALID_INPUT.getCode(), errorMessage);
    }

    /**
     * 2. HTTP 메서드 매칭이 안 될 때 (예: POST인데 GET으로 보냄) (405 Method Not Allowed)
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ApiResponse<?> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn("[Method Not Allowed] : {}", e.getMessage());
        // Enum에 정의된 METHOD_NOT_ALLOWED를 사용하도록 변경
        return ApiResponse.fail(SinnauErrorCode.METHOD_NOT_ALLOWED.getCode(),
                SinnauErrorCode.METHOD_NOT_ALLOWED.getMessage());
    }

    /**
     * 3. 컨트롤러 인자 타입이 맞지 않을 때 (예: 숫자 필드에 문자열 보냄) (400 Bad Request)
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.warn("[Type Mismatch] : {}", e.getMessage());

        return ApiResponse.fail(SinnauErrorCode.TYPE_MISMATCH.getCode(),
                String.format("%s: %s", SinnauErrorCode.TYPE_MISMATCH.getMessage(), e.getName()));
    }

    /**
     * 4. 커스텀 비즈니스 예외 처리 (우리가 직접 발생시키는 에러)
     */
    @ExceptionHandler(SinnauException.class) // BusinessException 클래스를 별도로 만드셔야 합니다.
    public ResponseEntity<ApiResponse<?>> handleSinnauException(SinnauException e) {
        log.error("[Business Error] : {}", e.getMessage());

        // 기본적으로 400 Bad Request를 반환하거나, Exception에 담긴 상태코드를 활용 가능
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(e.getCode(), e.getMessage()));
    }

    /**
     * 5. 최후의 보루: 위에서 처리하지 못한 모든 예외 (500 Internal Server Error)
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<?> handleException(Exception e) {
        log.error("[Unhandled Exception] : ", e);
        return ApiResponse.fail(SinnauErrorCode.SERVER_ERROR.getCode(), SinnauErrorCode.SERVER_ERROR.getMessage());
    }
}