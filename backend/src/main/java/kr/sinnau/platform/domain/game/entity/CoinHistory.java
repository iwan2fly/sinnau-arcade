package kr.sinnau.platform.domain.game.entity;

import kr.sinnau.platform.common.BaseTable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table("coin_history")
public class CoinHistory extends BaseTable {

    private Long userId;
    private Long amount;
    private String type; // EARN, SPEND
    private String reason; // GAME_WIN, GAME_BET, etc.

    @Builder
    public CoinHistory(Long userId, Long amount, String type, String reason) {
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.reason = reason;
        this.createdAt = LocalDateTime.now();
    }
}
