package kr.sinnau.platform.common.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender emailSender;
    private final ResourceLoader resourceLoader;

    /**
     * @param to 수신자 이메일
     * @param subject 메일 제목
     * @param templateName templates 폴더 내 HTML 파일명 (확장자 제외)
     * @param variables HTML에 전달할 변수 데이터 ({{key}} 형식으로 치환)
     */
    @Async
    public void sendEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        String htmlContent = loadAndProcessTemplate(templateName, variables);

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

    private String loadAndProcessTemplate(String templateName, Map<String, Object> variables) {
        try {
            Resource resource = resourceLoader.getResource("classpath:templates/" + templateName + ".html");
            String content = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                content = content.replace("{{" + entry.getKey() + "}}", entry.getValue().toString());
            }

            return content;
        } catch (IOException e) {
            log.error("템플릿 파일을 읽을 수 없습니다: {}", templateName);
            throw new RuntimeException("Template load failed", e);
        }
    }
}
