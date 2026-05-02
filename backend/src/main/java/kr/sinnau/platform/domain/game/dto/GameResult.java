package kr.sinnau.platform.domain.game.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GameResult {
    private Long gameId;         // 어떤 게임의 결과인지
    private boolean isSuccess;   // 성공 여부 (true/false)

    private Integer finalScore;  // 최종 점수
    private String rank;         // (선택) 달성 등급 (S, A, B...)

    private String resultMessage; // "탈락하셨습니다" 또는 "최고 기록 경신!"
    private Long totalPlayTime;   // 총 플레이 시간 (초)

    // 게임 종료 후 지급된 보상이 있다면
    private Long earnedExp;      // 획득 경험치
    private Integer earnedGold;  // 획득 골드
}