package kr.sinnau.platform.common.security;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;

public class JwtUtils {

    // 헤더 또는 쿠키에서 토큰을 가져오는 메서드
    public static String resolveToken(HttpServletRequest request) {
        // 1. 헤더에서 확인 (API 클라이언트용)
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        // 2. 쿠키에서 확인 (SSR 웹 브라우저용)
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    // 토큰의 유효성(만료 여부, 변조 여부) 검증
    public static boolean validateToken(String token, SecretKey secretKey) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            // 토큰이 위조되었거나 만료된 경우
            return false;
        }
    }

    // 토큰에서 이메일(Subject) 추출 로직 추가
    public static String getEmail(String token, SecretKey secretKey) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
