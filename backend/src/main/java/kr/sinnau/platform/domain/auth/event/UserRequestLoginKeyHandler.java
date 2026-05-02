package kr.sinnau.platform.domain.auth.event;

import kr.sinnau.platform.common.aspect.annotation.EventLog;
import kr.sinnau.platform.common.mail.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRequestLoginKeyHandler {

    private final MailService mailService;

    @Async // 별도 스레드에서 비동기 실행
    @EventLog("로그인 키 메일 발송")
    @EventListener
    public void handleLoginKeyEvent(UserRequestLoginKeyEvent event) {
        log.info("인증 메일 발송 이벤트 처리 시작: {}", event.email());

        // MailService에 정의된 범용 메서드 호출
        mailService.sendEmail(
                event.email(),
                "🎮 [신나유] 접속 인증번호입니다!",
                "template/mailTemplate",
                Map.of("code", event.code())
        );
    }
}
