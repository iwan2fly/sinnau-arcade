package kr.sinnau.platform.domain.game;

import kr.sinnau.platform.domain.auth.entity.User;
import kr.sinnau.platform.domain.auth.entity.dao.UserDao;
import kr.sinnau.platform.domain.game.dto.GameResponse;
import kr.sinnau.platform.domain.game.dto.GameResult;
import kr.sinnau.platform.domain.game.entity.CoinHistory;
import kr.sinnau.platform.domain.game.repository.CoinHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GameService {

    private final UserDao userDao;
    private final CoinHistoryRepository coinHistoryRepository;

    private static final long GAME_BET_AMOUNT = 100L;
    private static final long GAME_WIN_REWARD = 200L;

    @Transactional
    public GameResponse startGame(String email) {
        User user = userDao.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 1. 코인 차감
        user.subtractCoin(GAME_BET_AMOUNT);
        userDao.save(user);

        // 2. 히스토리 기록
        coinHistoryRepository.save(CoinHistory.builder()
                .userId(user.getId())
                .amount(GAME_BET_AMOUNT)
                .type("SPEND")
                .reason("GAME_BET")
                .build());

        return GameResponse.builder()
                .gameId(System.currentTimeMillis()) // 임시 게임 ID
                .status("PLAYING")
                .message("게임이 시작되었습니다!")
                .startTime(LocalDateTime.now())
                .build();
    }

    @Transactional
    public GameResult finishGame(String email, boolean isSuccess) {
        User user = userDao.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String message = isSuccess ? "승리하셨습니다!" : "패배하셨습니다.";
        int earnedGold = 0;

        if (isSuccess) {
            // 1. 보상 지급
            user.addCoin(GAME_WIN_REWARD);
            userDao.save(user);

            // 2. 히스토리 기록
            coinHistoryRepository.save(CoinHistory.builder()
                    .userId(user.getId())
                    .amount(GAME_WIN_REWARD)
                    .type("EARN")
                    .reason("GAME_WIN")
                    .build());
            
            earnedGold = (int) GAME_WIN_REWARD;
        }

        return GameResult.builder()
                .isSuccess(isSuccess)
                .resultMessage(message)
                .earnedGold(earnedGold)
                .build();
    }
}
