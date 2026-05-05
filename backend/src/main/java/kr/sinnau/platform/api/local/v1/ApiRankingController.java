package kr.sinnau.platform.api.local.v1;

import kr.sinnau.platform.common.dto.ApiResponse;
import kr.sinnau.platform.domain.game.RankingService;
import kr.sinnau.platform.domain.game.repository.RankingResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/local/v1/ranking")
@RequiredArgsConstructor
public class ApiRankingController {

    private final RankingService rankingService;

    @GetMapping
    public ApiResponse<List<RankingResult>> getRankings(
            @RequestParam String gameId,
            @RequestParam String category,
            @RequestParam String period) {
        return ApiResponse.ok(rankingService.getRankings(gameId, category, period));
    }
}
