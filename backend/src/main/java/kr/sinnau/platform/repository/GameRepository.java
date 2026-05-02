package kr.sinnau.platform.repository;


import org.springframework.stereotype.Repository;

import java.util.List;

//import static kr.sinnau.platform.generated.Tables.GAMES;

@Repository
public class GameRepository {


//    /**
//     * 새로운 게임 등록
//     * @param title 게임 이름 (예: 2048)
//     * @param isDescending 점수가 높을수록 좋으면 true (내림차순 정렬용)
//     */
//    public void save(String title, boolean isDescending) {
//        dsl.insertInto(GAMES)
//                .set(GAMES.TITLE, title)
//                .set(GAMES.IS_DESCENDING, isDescending)
//                .execute();
//    }
//
//    /**
//     * 게임 목록 페이지 단위 조회
//     */
//    public List<GamesRecord> findAllPaged(int page, int size) {
//        return dsl.selectFrom(GAMES)
//                .orderBy(GAMES.CREATED_AT.desc())
//                .limit(size)
//                .offset(page * size)
//                .fetch();
//    }
//
//    /**
//     * 특정 게임 정보 조회
//     */
//    public GamesRecord findById(Integer gameId) {
//        return dsl.selectFrom(GAMES)
//                .where(GAMES.ID.eq(gameId))
//                .fetchOne();
//    }
}