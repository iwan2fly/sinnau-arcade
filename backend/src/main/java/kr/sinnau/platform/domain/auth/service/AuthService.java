package kr.sinnau.platform.domain.auth.service;

import kr.sinnau.platform.api.local.v1.response.TokenSet;
import kr.sinnau.platform.common.interfaces.RefreshTokenRepository;
import kr.sinnau.platform.common.security.JwtProvider;
import kr.sinnau.platform.domain.auth.entity.AuthKey;
import kr.sinnau.platform.domain.auth.entity.User;
import kr.sinnau.platform.domain.auth.entity.UserRole;
import kr.sinnau.platform.domain.auth.entity.dao.*;
import kr.sinnau.platform.domain.auth.event.UserRequestLoginKeyEvent;
import kr.sinnau.platform.domain.auth.service.task.AuthKeyTask;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthKeyTask authKeyTask;
    private final AuthKeyDao authKeyDao;
    private final ApplicationEventPublisher eventPublisher;
    private final JwtProvider jwtProvider;
    private final UserDao userDao;
    private final UserRoleDao userRoleDao;
    private final RefreshTokenRepository refreshTokenRepository;
    private final kr.sinnau.platform.domain.game.repository.CoinHistoryRepository coinHistoryRepository;

    @Transactional
    public void sendVerificationCode(String email) {
        String code = authKeyTask.generateAndSaveKey(email);
        eventPublisher.publishEvent(new UserRequestLoginKeyEvent(email, code));
    }

    @Transactional
    public TokenSet verifyCodeAndIssueToken(String email, String loginKey) {

        // 1. 해당 이메일로 가장 최근에 발급된 인증키 조회
        AuthKey authKey = authKeyDao.findFirstByEmailOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new RuntimeException("인증 요청 기록이 없습니다."));

        // 2. 만료 시간 체크
        if (authKey.getExpireAt().isBefore(LocalDateTime.now())) {
            authKeyDao.deleteById(authKey.getId()); // 만료된 건 지워주기
            throw new RuntimeException("만료된 인증번호입니다.");
        }

        // 3. 번호 일치 여부 체크
        if (!authKey.getKeyString().equals(loginKey)) {
            throw new RuntimeException("인증번호가 일치하지 않습니다.");
        }

        // 4. 성공 처리: 키 삭제
        authKeyDao.deleteById(authKey.getId());

        // 5. 가입 완료 로직 호출! (이제야 실행됩니다)
        User user = userDao.findByEmail(email).orElseGet(() -> signUp(email, loginKey));

        // 6. 가입 축하 코인 지급 체크
        checkAndGrantWelcomeCoin(user);

        return issueTokens(email);
    }

    private void checkAndGrantWelcomeCoin(User user) {
        String welcomeReason = "WELCOME_COIN";
        if (!coinHistoryRepository.existsByUserIdAndReason(user.getId(), welcomeReason)) {
            long welcomeAmount = 10000L;
            user.addCoin(welcomeAmount);
            userDao.save(user);

            coinHistoryRepository.save(kr.sinnau.platform.domain.game.entity.CoinHistory.builder()
                    .userId(user.getId())
                    .amount(welcomeAmount)
                    .type("EARN")
                    .reason(welcomeReason)
                    .build());
        }
    }

    /**
     * 검증이 완료된 사용자에게 Access/Refresh 토큰을 발급하고
     * RefreshToken을 Caffeine 저장소에 보관합니다.
     */
    public TokenSet issueTokens(String email) {
        // 1. JWT 토큰 세트 생성
        String accessToken = jwtProvider.createAccessToken(email);
        String refreshToken = jwtProvider.createRefreshToken(email);

        // 2. Caffeine 저장소에 RefreshToken 저장 (나중에 재발급/로그아웃 시 확인용)
        long ttlMinutes = jwtProvider.getRefreshTokenExpiration() / (60 * 1000);
        refreshTokenRepository.save(email, refreshToken, ttlMinutes);

        // 3. 묶어서 반환
        return new TokenSet(accessToken, refreshToken);
    }

    /**
     * Refresh Token을 검증하고 새로운 Access/Refresh 토큰 세트를 발급합니다.
     */
    @Transactional
    public TokenSet refreshAccessToken(String refreshToken) {
        // 1. 토큰 자체의 유효성 검사 (JwtProvider 활용)
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new RuntimeException("유효하지 않거나 만료된 리프레시 토큰입니다.");
        }

        // 2. 토큰에서 사용자 식별 정보(email) 추출
        String email = jwtProvider.getEmail(refreshToken);

        // 3. [수정됨] 인터페이스 명칭에 맞춰 findByEmail 사용
        String savedToken = refreshTokenRepository.findByEmail(email);

        // 4. 저장된 토큰이 없거나, 전달받은 토큰과 일치하지 않으면 탈퇴/로그아웃 처리된 것으로 간주
        if (savedToken == null || !savedToken.equals(refreshToken)) {
            throw new RuntimeException("리프레시 토큰이 일치하지 않거나 서버 세션이 만료되었습니다.");
        }

        // 5. 검증 완료! 새로운 토큰 세트 발급 및 저장소 갱신
        // 기존에 작성하신 issueTokens(email) 내부에서 refreshTokenRepository.save를 호출하므로 그대로 재사용합니다.
        return issueTokens(email);
    }

    @Transactional // 중요: 유저 생성과 권한 부여는 한 몸이어야 합니다!
    public User signUp(String email, String loginKey) {

        String nickName = generateRandomNickname() + "_" + loginKey;

        // 1. 유저 생성 (BaseTable의 createdAt은 빌더나 생성자에서 세팅)
        User newUser = User.builder()
                .email(email)
                .nickname(nickName)
                .userStatus("ACTIVE") // 자바에서 세팅하기로 한 기본값
                .termsVersion("v1.0")
                .termsAgreedAt(LocalDateTime.now())
                .build();

        // 2. 유저 저장 (여기서 @Table("user") 에러가 터질 지켜보세요!)
        User savedUser = userDao.save(newUser);

        // 3. 기본 권한(USER) 부여
        UserRole defaultRole = UserRole.builder()
                .userId(savedUser.getId()) // 저장된 유저의 ID 활용
                .roleCode("USER")
                .build();

        userRoleDao.save(defaultRole);

        return savedUser;
    }



    private String generateRandomNickname() {
        List<String> adjectives = List.of("신나는", "행복한", "배고픈", "똑똑한", "멋진", "포근한");
        List<String> nouns = List.of("너구리", "개발자", "감자", "고양이", "쿼카", "나무보늘");

        String adj = adjectives.get((int) (Math.random() * adjectives.size()));
        String noun = nouns.get((int) (Math.random() * nouns.size()));
        return adj + noun; // 예: 신나는너구리
    }
}
