package kr.sinnau.platform.domain.auth.service.task;

import kr.sinnau.platform.domain.auth.entity.AuthKey;
import kr.sinnau.platform.domain.auth.entity.dao.AuthKeyDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AuthKeyTask {

    private final AuthKeyDao authKeyDao;

    /**
     * 인증키를 생성/저장하고 그 값을 리턴합니다.
     */
    public String generateAndSaveKey(String email) {
        // 1. 번호 생성
        String code = String.valueOf((int)(Math.random() * 899999) + 100000);

        // 2. 엔티티 빌드
        AuthKey authKey = AuthKey.builder()
                .email(email)
                .keyString(code)
                .expireAt(LocalDateTime.now().plusSeconds(300))
                .build();

        // 3. ID 세팅 및 저장
        System.out.println("authKey: " + authKey.toPrettyJson());
        authKeyDao.save(authKey);

        return code;
    }

}
