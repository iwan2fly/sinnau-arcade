package kr.sinnau.platform.domain.game.repository;

/**
 * SQL 순위 쿼리 결과를 매핑하기 위한 Projection 인터페이스
 */
public interface RankingResult {
    String getNickName();
    Double getValue();
}
