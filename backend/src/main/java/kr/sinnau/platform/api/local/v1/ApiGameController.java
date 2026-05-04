package kr.sinnau.platform.api.local.v1;

import kr.sinnau.platform.common.dto.ApiResponse;
import kr.sinnau.platform.domain.game.GameService;
import kr.sinnau.platform.domain.game.dto.GameResponse;
import kr.sinnau.platform.domain.game.dto.GameResult;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/local/v1/game")
@RequiredArgsConstructor
public class ApiGameController {

    private final GameService gameService;

    @PostMapping("/start")
    public ApiResponse<GameResponse> startGame(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "COIN_FLIP") String gameId,
            @RequestParam(defaultValue = "100") long betAmount) {
        return ApiResponse.ok(gameService.startGame(userDetails.getUsername(), gameId, betAmount));
    }

    @PostMapping("/finish")
    public ApiResponse<GameResult> finishGame(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "COIN_FLIP") String gameId,
            @RequestParam boolean isSuccess,
            @RequestParam(defaultValue = "100") long betAmount) {
        return ApiResponse.ok(gameService.finishGame(userDetails.getUsername(), gameId, isSuccess, betAmount));
    }
}
