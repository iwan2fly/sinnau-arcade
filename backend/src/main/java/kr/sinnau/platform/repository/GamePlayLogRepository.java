package kr.sinnau.platform.repository;


import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

//import static kr.sinnau.platform.generated.Tables.GAME_PLAY_LOG; // 단수형 확인!
//import static org.jooq.impl.DSL.max;

@Repository
public class GamePlayLogRepository {

//    /**
//     * 게임 결과 저장
//     */
//    public void saveLog(Integer userId, Integer gameId, Long score) {
//        dsl.insertInto(GAME_PLAY_LOG)
//                .set(GAME_PLAY_LOG.USER_ID, userId)
//                .set(GAME_PLAY_LOG.GAME_ID, gameId)
//                .set(GAME_PLAY_LOG.SCORE, score)
//                .execute();
//    }
//
//    /**
//     * 특정 게임의 명예의 전당 (Top 10)
//     */
//    public Result<Record3<Integer, Long, LocalDateTime>> getTopRankings(Integer gameId) {
//        return dsl.select(
//                        GAME_PLAY_LOG.USER_ID,
//                        max(GAME_PLAY_LOG.SCORE).as("best_score"),
//                        max(GAME_PLAY_LOG.CREATED_AT).as("last_played_at")
//                )
//                .from(GAME_PLAY_LOG)
//                .where(GAME_PLAY_LOG.GAME_ID.eq(gameId))
//                .groupBy(GAME_PLAY_LOG.USER_ID)
//                .orderBy(max(GAME_PLAY_LOG.SCORE).desc())
//                .limit(10)
//                .fetch();
//    }
}