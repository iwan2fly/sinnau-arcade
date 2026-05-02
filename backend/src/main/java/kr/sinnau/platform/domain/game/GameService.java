package kr.sinnau.platform.domain.game;

import kr.sinnau.platform.repository.GameRepository;
import kr.sinnau.platform.domain.game.dto.GameResponse;
import kr.sinnau.platform.domain.game.dto.GameResult;

import java.time.Duration;
import java.time.LocalDateTime;

public class GameService {


    // 1. 게임 시작
    public GameResponse startGame(Long userId) {
        // 실제로는 DB에 저장하고 생성된 ID를 가져와야 함
        Long gameId = 1L;

        // 현재 시간을 시작 시간으로 설정
        LocalDateTime startTime = LocalDateTime.now();

        // 빌더 패턴이나 생성자를 사용하여 Response 생성
        return GameResponse.builder()
                .gameId(gameId)
                .status("PLAYING")
                .message("게임이 시작되었습니다!")
                .startTime(startTime)
                .build();
    }


}
