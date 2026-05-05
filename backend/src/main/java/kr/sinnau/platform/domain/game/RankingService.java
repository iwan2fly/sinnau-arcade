package kr.sinnau.platform.domain.game;

import kr.sinnau.platform.domain.game.repository.GamePlayLogRepository;
import kr.sinnau.platform.domain.game.repository.RankingResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final GamePlayLogRepository gamePlayLogRepository;

    public List<RankingResult> getRankings(String gameId, String category, String period) {
        LocalDateTime since = getSinceDate(period);
        int limit = 10;

        return switch (category.toLowerCase()) {
            case "profit" -> gamePlayLogRepository.getProfitRanking
                    (gameId, since, limit);
            case "games" -> gamePlayLogRepository.getGamesRanking(gameId, since, limit);
            case "winrate" -> gamePlayLogRepository.getWinRateRanking(gameId, since, 5, limit); // Min 5 games for win rate
            default -> new ArrayList<>();
        };
    }

    private LocalDateTime getSinceDate(String period) {
        LocalDateTime now = LocalDateTime.now();
        return switch (period.toLowerCase()) {
            case "today" -> now.withHour(0).withMinute(0).withSecond(0).withNano(0);
            case "week" -> now.minusWeeks(1);
            case "month" -> now.minusMonths(1);
            case "year" -> now.minusYears(1);
            default -> now.minusYears(100); // All time
        };
    }
}
