package kr.sinnau.platform.domain.auth.entity.dao;

import io.jsonwebtoken.lang.Assert;
import kr.sinnau.platform.domain.auth.entity.AuthKey;
import kr.sinnau.platform.domain.auth.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthKeyDao {

    private final AuthKeyRepository authKeyRepository;

    public AuthKey save(AuthKey authKey) {
        return authKeyRepository.save(authKey);
    }

    public long deleteById(Long id) {
        Assert.notNull("'id' required");
        authKeyRepository.deleteById(id);
        return id;
    }

    public Optional<AuthKey> findFirstByEmailOrderByCreatedAtDesc(String email) {
        Assert.hasText(email, "'email' required");
        return authKeyRepository.findFirstByEmailOrderByCreatedAtDesc(email);
    }

    public int deleteByEmail(String email) {
        Assert.hasText(email, "'email' required");
        return authKeyRepository.deleteByEmail(email);
    }
}
