package kr.sinnau.platform.domain.auth.entity;

import kr.sinnau.platform.common.BaseTable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table("nickname_history")
public class NicknameHistory extends BaseTable {

    private Long userId;
    private String beforeNickname;
    private String afterNickname;

    @Builder
    public NicknameHistory(Long userId, String beforeNickname, String afterNickname) {
        this.userId = userId;
        this.beforeNickname = beforeNickname;
        this.afterNickname = afterNickname;
        this.createdAt = LocalDateTime.now();
    }
}
