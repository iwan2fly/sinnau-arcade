package kr.sinnau.platform.domain.auth.entity.dao;


import kr.sinnau.platform.domain.auth.entity.UserRole;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface UserRoleRepository extends ListCrudRepository<UserRole, Long> {

    // 특정 사용자가 어떤 권한들을 가지고 있는지 조회할 때 사용합니다.
    List<UserRole> findByUserId(Long userId);
}