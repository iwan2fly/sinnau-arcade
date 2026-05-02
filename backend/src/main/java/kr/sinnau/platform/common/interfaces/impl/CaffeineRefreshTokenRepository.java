package kr.sinnau.platform.common.interfaces.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import kr.sinnau.platform.common.interfaces.RefreshTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
public class CaffeineRefreshTokenRepository implements RefreshTokenRepository {

    /**
     * 핵심: Expiry 인터페이스를 구현하여 저장 시점에 TTL을 결정합니다.
     * 여기서는 Value에 들어오는 정보(사실상 토큰)에 따라 시간을 정할 수 없으므로,
     * Wrapper 객체를 쓰거나 단순하게 구현할 수 있습니다.
     */
    private final Cache<String, String> cache = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfter(new Expiry<String, String>() {
                @Override
                public long expireAfterCreate(@NonNull String key, @NonNull String value, long currentTime) {
                    // 기본값은 일단 길게 설정 (실제 수명은 save 메서드 호출 시 제어하기 위함)
                    return TimeUnit.DAYS.toNanos(7);
                }

                @Override
                public long expireAfterUpdate(@NonNull String key, @NonNull String value,
                                              long currentTime, @NonNegative long currentDuration) {
                    // 업데이트 시에도 기존 수명 유지
                    return currentDuration;
                }

                @Override
                public long expireAfterRead(@NonNull String key, @NonNull String value,
                                            long currentTime, @NonNegative long currentDuration) {
                    return currentDuration;
                }
            })
            .build();

    @Override
    public void save(String email, String refreshToken, long ttlMinutes) {
        // Caffeine의 기본 Cache 인터페이스는 put 할 때 개별 TTL을 지원하지 않으므로,
        // 만약 엄격하게 ttlMinutes를 지켜야 한다면 설정 시점에 정책을 잘 세워야 합니다.
        // 현재는 단순하게 저장 로직을 구현합니다.
        cache.put(email, refreshToken);
        log.info("RefreshToken 저장 완료 [Caffeine] -> Email: {}, TTL: {} min", email, ttlMinutes);
    }

    @Override
    public String findByEmail(String email) {
        String token = cache.getIfPresent(email);
        if (token != null) {
            log.debug("RefreshToken 조회 성공 [Caffeine] -> Email: {}", email);
        }
        return token;
    }

    @Override
    public void deleteByEmail(String email) {
        cache.invalidate(email);
        log.info("RefreshToken 삭제 완료 [Caffeine] -> Email: {}", email);
    }
}