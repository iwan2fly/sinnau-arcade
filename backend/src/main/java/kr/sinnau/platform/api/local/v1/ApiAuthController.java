package kr.sinnau.platform.api.local.v1;

import jakarta.servlet.http.HttpServletResponse;
import kr.sinnau.platform.api.local.v1.request.VerifyLoginKeyRequest;
import kr.sinnau.platform.api.local.v1.response.LoginResponse;
import kr.sinnau.platform.api.local.v1.response.TokenSet;
import kr.sinnau.platform.common.dto.ApiResponse;
import kr.sinnau.platform.common.security.JwtProvider;
import kr.sinnau.platform.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/local/v1")
@RequiredArgsConstructor
public class ApiAuthController {

    @Value("${platform.cookie.secure}")
    private boolean isCookieSecure;

    @Value("${platform.cookie.same-site}")
    private String cookieSameSite;

    private final AuthService authService;
    private final JwtProvider jwtProvider;


    /**
     * 1. 인증 코드 발송 요청
     */
    @PostMapping("/auth/send-code/{email}")
    public ApiResponse<?> sendCode(@PathVariable String email) {
        authService.sendVerificationCode(email);
        return ApiResponse.ok(); // 성공 응답 (data: null)
    }

    /**
     * 2. 인증 코드 검증 요청
     */
    @PostMapping("/auth/verify")
    public ApiResponse<LoginResponse> verify(
            @RequestBody VerifyLoginKeyRequest request,
            HttpServletResponse httpResponse // 쿠키를 굽기 위해 추가
    ) {
        log.debug(request.toString());
        TokenSet tokenSet = authService.verifyCodeAndIssueToken(request.getEmail(), request.getKeyString());

        // 1. RefreshToken을 HttpOnly 쿠키로 생성
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", tokenSet.refreshToken())
                .httpOnly(true)
                .secure(isCookieSecure) // HTTPS에서만 전송
                .path("/")
                .maxAge(jwtProvider.getRefreshTokenExpiration() / 1000)
                .sameSite(cookieSameSite) // CSRF 방지
                .build();
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        // 2. AccessToken도 HttpOnly 쿠키로 생성 (옵션 B)
        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", tokenSet.accessToken())
                .httpOnly(true)
                .secure(isCookieSecure)
                .path("/")
                .maxAge(jwtProvider.getAccessTokenExpiration() / 1000)
                .sameSite(cookieSameSite)
                .build();
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());

        // 3. 프론트엔드 식별용 상태 쿠키 (HttpOnly 아님)
        ResponseCookie loggedInCookie = ResponseCookie.from("isLoggedIn", "true")
                .httpOnly(false)
                .secure(isCookieSecure)
                .path("/")
                .maxAge(jwtProvider.getAccessTokenExpiration() / 1000)
                .sameSite(cookieSameSite)
                .build();
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, loggedInCookie.toString());

        // Body에는 이제 토큰을 내려주지 않아도 됨
        LoginResponse response = new LoginResponse(
                "Bearer",
                null, // 쿠키로 보냈으니 Body에서는 뺌
                null, 
                jwtProvider.getAccessTokenExpiration() / 1000
        );

        return ApiResponse.ok(response);
    }

    @PostMapping("/auth/refresh")
    public ApiResponse<LoginResponse> refresh(
            @CookieValue(name = "refreshToken") String refreshToken, // 쿠키에서 쏙 빼오기
            HttpServletResponse httpResponse
    ) {
        TokenSet newTokenSet = authService.refreshAccessToken(refreshToken);

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", newTokenSet.refreshToken())
                .httpOnly(true)
                .secure(isCookieSecure)
                .path("/")
                .maxAge(jwtProvider.getRefreshTokenExpiration() / 1000)
                .sameSite(cookieSameSite)
                .build();
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", newTokenSet.accessToken())
                .httpOnly(true)
                .secure(isCookieSecure)
                .path("/")
                .maxAge(jwtProvider.getAccessTokenExpiration() / 1000)
                .sameSite(cookieSameSite)
                .build();
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());

        ResponseCookie loggedInCookie = ResponseCookie.from("isLoggedIn", "true")
                .httpOnly(false)
                .secure(isCookieSecure)
                .path("/")
                .maxAge(jwtProvider.getAccessTokenExpiration() / 1000)
                .sameSite(cookieSameSite)
                .build();
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, loggedInCookie.toString());

        LoginResponse response = new LoginResponse(
                "Bearer",
                null,
                null,
                jwtProvider.getAccessTokenExpiration() / 1000
        );

        return ApiResponse.ok(response);
    }

    @PostMapping("/auth/logout")
    public ApiResponse<?> logout(HttpServletResponse httpResponse) {
        // 쿠키 만료시켜서 삭제
        ResponseCookie deleteRefresh = ResponseCookie.from("refreshToken", "")
                .httpOnly(true).secure(isCookieSecure).path("/").maxAge(0).sameSite(cookieSameSite).build();
        ResponseCookie deleteAccess = ResponseCookie.from("accessToken", "")
                .httpOnly(true).secure(isCookieSecure).path("/").maxAge(0).sameSite(cookieSameSite).build();
        ResponseCookie deleteLoggedIn = ResponseCookie.from("isLoggedIn", "")
                .httpOnly(false).secure(isCookieSecure).path("/").maxAge(0).sameSite(cookieSameSite).build();

        httpResponse.addHeader(HttpHeaders.SET_COOKIE, deleteRefresh.toString());
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, deleteAccess.toString());
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, deleteLoggedIn.toString());

        return ApiResponse.ok();
    }
}