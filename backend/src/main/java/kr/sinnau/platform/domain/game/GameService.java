package kr.sinnau.platform.domain.game;

import kr.sinnau.platform.domain.auth.entity.User;
import kr.sinnau.platform.domain.auth.entity.dao.UserDao;
import kr.sinnau.platform.domain.game.dto.GameResponse;
import kr.sinnau.platform.domain.game.dto.GameResult;
import kr.sinnau.platform.domain.game.entity.CoinHistory;
import kr.sinnau.platform.domain.game.entity.GamePlayLog;
import kr.sinnau.platform.domain.game.repository.CoinHistoryRepository;
import kr.sinnau.platform.domain.game.repository.GamePlayLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GameService {

    private final UserDao userDao;
    private final CoinHistoryRepository coinHistoryRepository;
    private final GamePlayLogRepository gamePlayLogRepository;

    @Transactional
    public GameResponse startGame(String email, String gameId, long betAmount) {
        if (betAmount < 100) {
            throw new RuntimeException("Minimum bet is 100 coins");
        }

        User user = userDao.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 1. 코인 차감
        user.subtractCoin(betAmount);
        userDao.save(user);

        // 2. 히스토리 기록
        coinHistoryRepository.save(CoinHistory.builder()
                .userId(user.getId())
                .amount(betAmount)
                .type("SPEND")
                .reason("GAME_BET")
                .build());

        return GameResponse.builder()
                .gameId(System.currentTimeMillis()) // 임시 세션 ID
                .status("PLAYING")
                .message("게임이 시작되었습니다!")
                .startTime(LocalDateTime.now())
                .build();
    }

    @Transactional
    public GameResult finishGame(String email, String gameId, boolean isSuccess, long betAmount) {
        User user = userDao.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String message = isSuccess ? "승리하셨습니다!" : "패배하셨습니다.";
        long earnedAmount = 0;

        if (isSuccess) {
            // 보상은 베팅액의 2배
            earnedAmount = betAmount * 2;
            
            // 1. 보상 지급
            user.addCoin(earnedAmount);
            userDao.save(user);

            // 2. 히스토리 기록
            coinHistoryRepository.save(CoinHistory.builder()
                    .userId(user.getId())
                    .amount(earnedAmount)
                    .type("EARN")
                    .reason("GAME_WIN")
                    .build());
        }

        // 3. 게임 플레이 로그 저장
        gamePlayLogRepository.save(GamePlayLog.builder()
                .userId(user.getId())
                .gameId(gameId)
                .result(isSuccess ? "WIN" : "LOSS")
                .spentAmount(betAmount)
                .earnedAmount(earnedAmount)
                .build());

        return GameResult.builder()
                .isSuccess(isSuccess)
                .resultMessage(message)
                .earnedGold((int) earnedAmount)
                .build();
    }
}
