package kr.sinnau.platform.common.interfaces;

public interface RefreshTokenRepository {
    void save(String email, String refreshToken, long ttlMinutes);
    String findByEmail(String email);
    void deleteByEmail(String email);
}