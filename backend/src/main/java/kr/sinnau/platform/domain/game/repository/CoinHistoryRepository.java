package kr.sinnau.platform.domain.game.repository;

import kr.sinnau.platform.domain.game.entity.CoinHistory;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoinHistoryRepository extends ListCrudRepository<CoinHistory, Long> {
    boolean existsByUserIdAndReason(Long userId, String reason);
}
