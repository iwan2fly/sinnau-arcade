package kr.sinnau.platform.domain.auth.entity.dao;

import io.jsonwebtoken.lang.Assert;
import kr.sinnau.platform.domain.auth.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserDao {

    private final UserRepository userRepository;

    public User save(User user) {
        return userRepository.save(user);
    }

    // 이메일로 가입 여부 확인 및 로그인 시 사용
    public Optional<User> findByEmail(String email) {
        Assert.hasText(email, "'email' required");
        return userRepository.findByEmail(email);
    }

    public boolean existsByNickname(String nickname) {
        Assert.hasText(nickname, "'nickname' required");
        return userRepository.existsByNickname(nickname);
    }
}
