package kr.sinnau.platform.domain.auth.entity.dao;

import io.jsonwebtoken.lang.Assert;
import kr.sinnau.platform.domain.auth.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserRoleDao {

    private final UserRoleRepository userRoleRepository;

    public UserRole save(UserRole usrRole) {
        return userRoleRepository.save(usrRole);
    }

    public List<UserRole> findByUserId(Long userId) {
        Assert.notNull(userId, "'userId' required");
        return userRoleRepository.findByUserId(userId);
    }
}
