package kr.sinnau.platform.common;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.*;

import java.time.LocalDateTime;

@Getter
public class BaseTable extends BaseModel {
    @Id
    @Setter
    protected Long id;

    @CreatedBy
    protected String createUserId;

    @LastModifiedBy
    protected String updateUserId;

    @CreatedDate
    protected LocalDateTime createdAt;

    @LastModifiedDate
    protected LocalDateTime updatedAt;
}
