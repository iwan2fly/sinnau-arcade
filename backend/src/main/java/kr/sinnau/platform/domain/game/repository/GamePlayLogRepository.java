package kr.sinnau.platform.domain.game.repository;

import kr.sinnau.platform.domain.game.entity.GamePlayLog;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface GamePlayLogRepository extends CrudRepository<GamePlayLog, Long> {

    @Query("""
        SELECT 
            u.nickname as nick_name,
            SUM(g.earned_amount - g.spent_amount) as value
        FROM game_play_log g
        JOIN "user" u ON g.user_id = u.id
        WHERE g.game_id = :gameId
          AND g.created_at >= :since
        GROUP BY u.id, u.nickname
        ORDER BY value DESC
        LIMIT :limit
    """)
    List<RankingResult> getProfitRanking(@Param("gameId") String gameId, 
                                         @Param("since") LocalDateTime since, 
                                         @Param("limit") int limit);

    @Query("""
        SELECT 
            u.nickname as nick_name,
            COUNT(g.id) as value
        FROM game_play_log g
        JOIN "user" u ON g.user_id = u.id
        WHERE g.game_id = :gameId
          AND g.created_at >= :since
        GROUP BY u.id, u.nickname
        ORDER BY value DESC
        LIMIT :limit
    """)
    List<RankingResult> getGamesRanking(@Param("gameId") String gameId, 
                                        @Param("since") LocalDateTime since, 
                                        @Param("limit") int limit);

    @Query("""
        SELECT 
            u.nickname as nick_name,
            ROUND(CAST(SUM(CASE WHEN g.result = 'WIN' THEN 1 ELSE 0 END) AS NUMERIC) / COUNT(g.id) * 100, 1) as value
        FROM game_play_log g
        JOIN "user" u ON g.user_id = u.id
        WHERE g.game_id = :gameId
          AND g.created_at >= :since
        GROUP BY u.id, u.nickname
        HAVING COUNT(g.id) >= :minGames
        ORDER BY value DESC
        LIMIT :limit
    """)
    List<RankingResult> getWinRateRanking(@Param("gameId") String gameId, 
                                          @Param("since") LocalDateTime since, 
                                          @Param("minGames") int minGames,
                                          @Param("limit") int limit);
}
