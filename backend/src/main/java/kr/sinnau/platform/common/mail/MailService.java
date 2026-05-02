package kr.sinnau.platform.common.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;

    /**
     * @param to 수신자 이메일
     * @param subject 메일 제목
     * @param templateName templates 폴더 내 HTML 파일명
     * @param variables HTML에 전달할 변수 데이터
     */
    @Async // 이메일 발송은 시간이 걸리므로 비동기 처리를 추천합니다!
    public void sendEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);
        String htmlContent = templateEngine.process(templateName, context);

        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("iwan2fly@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            emailSender.send(message);
        } catch (MessagingException e) {
            log.error("메일 발송 실패: {}", e.getMessage());
            throw new RuntimeException("Email send failed", e);
        }
    }
}
