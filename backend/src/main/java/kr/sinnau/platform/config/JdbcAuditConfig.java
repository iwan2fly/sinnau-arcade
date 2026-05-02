package kr.sinnau.platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;

import java.util.Optional;

@Configuration
@EnableJdbcAuditing // 이거 꼭 있어야 함!
public class JdbcAuditConfig {
    @Bean
    public AuditorAware<String> auditorProvider() {
        // 지금은 구현 전이니 임시로 "system"이나 "anonymous"를 반환하게 만듭니다.
        // 나중에 Spring Security 연동하면 SecurityContextHolder에서 유저 ID를 가져오면 됩니다.
        return () -> Optional.of("sinnau_user");
    }
}
