package kr.sinnau.platform.domain.game.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GameResponse {
    // 게임 세션을 식별할 ID (종료 처리 시 필요)
    private Long gameId;

    // 현재 게임의 상태 (START, SUCCESS, FAIL 등)
    private String status;

    // 사용자에게 보여줄 메시지
    private String message;

    // 게임 시작 시간
    private LocalDateTime startTime;

    // 게임 종료 시간 (종료 시에만 값이 들어감)
    private LocalDateTime endTime;

    // 플레이 소요 시간 (초 단위 등)
    private Long playTimeSeconds;

    // 결과 데이터 (필요 시 점수나 보상 정보)
    private Integer score;
}