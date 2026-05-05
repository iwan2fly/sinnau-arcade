package kr.sinnau.platform.domain.game.repository;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * SQL 순위 쿼리 결과를 매핑하기 위한 Projection 인터페이스
 */
public interface RankingResult {
    @JsonProperty("nick_name")
    String getNickName();
    Double getValue();
}
