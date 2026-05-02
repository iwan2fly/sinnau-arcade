package kr.sinnau.platform.domain.auth.entity.dao;


import kr.sinnau.platform.domain.auth.entity.User;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
interface UserRepository extends ListCrudRepository<User, Long> {

    // 이메일로 가입 여부 확인 및 로그인 시 사용
    Optional<User> findByEmail(String email);

    // 닉네임 중복 검사 시 사용
    boolean existsByNickname(String nickname);
}