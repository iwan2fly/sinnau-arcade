package kr.sinnau.platform.domain.auth.entity.dao;

import kr.sinnau.platform.domain.auth.entity.NicknameHistory;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NicknameHistoryRepository extends ListCrudRepository<NicknameHistory, Long> {
    boolean existsByUserId(Long userId);
}
