package kr.sinnau.platform.domain.auth.entity;

import kr.sinnau.platform.common.BaseTable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table("user")
public class User extends BaseTable {

    private String email;
    private String nickname;
    private String userStatus;
    private String profileImageUrl;
    private Long coin;

    private LocalDateTime lastLoginAt;
    private String termsVersion;
    private LocalDateTime termsAgreedAt;

    public void addCoin(long amount) {
        if (this.coin == null) this.coin = 0L;
        this.coin += amount;
    }

    public void subtractCoin(long amount) {
        if (this.coin == null || this.coin < amount) {
            throw new RuntimeException("Not enough coins");
        }
        this.coin -= amount;
    }

    public void changeNickname(String newNickname) {
        this.nickname = newNickname;
    }

    @Builder
    public User(String email, String nickname, String userStatus, Long coin,
                String termsVersion, LocalDateTime termsAgreedAt) {
        this.email = email;
        this.nickname = nickname;
        this.userStatus = userStatus;
        this.coin = coin;
        this.termsVersion = termsVersion;
        this.termsAgreedAt = termsAgreedAt;
        this.createdAt = LocalDateTime.now(); // 가입 시점 세팅
    }
}
