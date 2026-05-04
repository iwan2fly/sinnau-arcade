package kr.sinnau.platform.api.local.v1;

import kr.sinnau.platform.api.local.v1.response.ProfileResponse;
import kr.sinnau.platform.common.dto.ApiResponse;
import kr.sinnau.platform.domain.auth.entity.NicknameHistory;
import kr.sinnau.platform.domain.auth.entity.User;
import kr.sinnau.platform.domain.auth.entity.dao.NicknameHistoryRepository;
import kr.sinnau.platform.domain.auth.entity.dao.UserDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/local/v1")
@RequiredArgsConstructor
public class ApiProfileController {

    private final UserDao userDao;
    private final NicknameHistoryRepository nicknameHistoryRepository;

    @GetMapping("/profile/me")
    public ApiResponse<ProfileResponse> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        User user = userDao.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean needsNicknameSetup = !nicknameHistoryRepository.existsByUserId(user.getId());

        log.info("User {} has coin balance: {}, needsNicknameSetup: {}", email, user.getCoin(), needsNicknameSetup);

        return ApiResponse.ok(new ProfileResponse(
                user.getNickname(),
                String.valueOf(user.getCoin() == null ? 0 : user.getCoin()),
                needsNicknameSetup
        ));
    }

    @PostMapping("/profile/nickname")
    @Transactional
    public ApiResponse<?> updateNickname(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> request) {
        
        String newNickname = request.get("newNickname");
        if (newNickname == null || newNickname.trim().isEmpty()) {
            return ApiResponse.fail("400", "닉네임을 입력해주세요.");
        }

        if (userDao.existsByNickname(newNickname)) {
            return ApiResponse.fail("409", "이미 사용 중인 닉네임입니다.");
        }

        String email = userDetails.getUsername();
        User user = userDao.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String oldNickname = user.getNickname();

        // 1. 유저 닉네임 업데이트
        user.changeNickname(newNickname);
        userDao.save(user);

        // 2. 닉네임 변경 이력 저장
        nicknameHistoryRepository.save(NicknameHistory.builder()
                .userId(user.getId())
                .beforeNickname(oldNickname)
                .afterNickname(newNickname)
                .build());

        log.info("User {} changed nickname from {} to {}", email, oldNickname, newNickname);

        return ApiResponse.ok();
    }
}
