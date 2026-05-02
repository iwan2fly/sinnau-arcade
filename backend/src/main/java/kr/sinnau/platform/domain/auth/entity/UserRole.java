package kr.sinnau.platform.domain.auth.entity;

import kr.sinnau.platform.common.BaseTable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table("user_role")
public class UserRole extends BaseTable {

    private Long userId;   // user 테이블의 id 참조
    private String roleCode; // 'USER', 'ADMIN' 등

    @Builder
    public UserRole(Long userId, String roleCode) {
        this.userId = userId;
        this.roleCode = roleCode;
        this.createdAt = LocalDateTime.now();
    }
}
