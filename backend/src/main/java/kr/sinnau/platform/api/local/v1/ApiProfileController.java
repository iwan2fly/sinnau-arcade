package kr.sinnau.platform.api.local.v1;

import kr.sinnau.platform.api.local.v1.request.VerifyLoginKeyRequest;
import kr.sinnau.platform.api.local.v1.response.LoginResponse;
import kr.sinnau.platform.api.local.v1.response.ProfileResponse;
import kr.sinnau.platform.api.local.v1.response.TokenSet;
import kr.sinnau.platform.common.dto.ApiResponse;
import kr.sinnau.platform.common.security.JwtProvider;
import kr.sinnau.platform.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/local/v1")
@RequiredArgsConstructor
public class ApiProfileController {

    private final AuthService authService;
    private final JwtProvider jwtProvider;

    @GetMapping("/profile/me")
    public ApiResponse<ProfileResponse> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        // 토큰을 통해 인증된 사용자의 이메일(또는 ID)로 조회
//        String email = userDetails.getUsername();
//        Member member = memberService.findByEmail(email);

        return ApiResponse.ok(new ProfileResponse(
                "testNick",
                "1000" // 🪙 아까 논의한 코인 정보!
        ));
    }
}