package kr.sinnau.platform.domain.game.entity;

import kr.sinnau.platform.common.BaseTable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table("game_play_log")
public class GamePlayLog extends BaseTable {

    private Long userId;
    private String gameId; // e.g., "COIN_FLIP"
    private String result; // WIN, LOSS
    private Long spentAmount;
    private Long earnedAmount;
    private Double score; // For games with points, can be null

    @Builder
    public GamePlayLog(Long userId, String gameId, String result, Long spentAmount, Long earnedAmount, Double score) {
        this.userId = userId;
        this.gameId = gameId;
        this.result = result;
        this.spentAmount = spentAmount;
        this.earnedAmount = earnedAmount;
        this.score = score;
        this.createdAt = LocalDateTime.now();
    }
}
