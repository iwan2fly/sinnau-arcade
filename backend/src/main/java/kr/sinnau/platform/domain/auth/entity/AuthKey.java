package kr.sinnau.platform.domain.auth.entity;

import kr.sinnau.platform.common.BaseTable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table("auth_key")
public class AuthKey extends BaseTable {

    private String email;
    private String keyString;
    private LocalDateTime expireAt;

    @Builder
    public AuthKey(String email, String keyString, LocalDateTime expireAt) {
        this.email = email;
        this.keyString = keyString;
        this.expireAt = expireAt;
    }


}
