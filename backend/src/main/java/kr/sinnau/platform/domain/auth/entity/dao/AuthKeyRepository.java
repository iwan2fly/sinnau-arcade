package kr.sinnau.platform.domain.auth.entity.dao;

import kr.sinnau.platform.domain.auth.entity.AuthKey;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface AuthKeyRepository extends ListCrudRepository<AuthKey, Long> {
    // 기본적으로 save(), findById(), delete() 등을 모두 제공합니다.

//    @Modifying // 데이터를 변경하는 쿼리임을 명시
//    @Query("""
//            INSERT INTO auth_key (email, key_string, expire_at, created_at)
//            VALUES (:#{#authKey.email}, :#{#authKey.keyString}, :#{#authKey.expireAt}, NOW())
//        """)
//    void insert(@Param("authKey") AuthKey authKey);

    // 이메일로 가장 최근 인증키 찾기
    Optional<AuthKey> findFirstByEmailOrderByCreatedAtDesc(String email);

    @Modifying
    @Query("DELETE FROM auth_key WHERE email = :email")
    int deleteByEmail(@Param("email") String email);
}
