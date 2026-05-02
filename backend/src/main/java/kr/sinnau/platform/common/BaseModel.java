package kr.sinnau.platform.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public abstract class BaseModel {

    // 공유해서 사용할 ObjectMapper 설정
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule()) // LocalDateTime 변환을 위해 필수!
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 날짜를 이쁘게 출력

    public String toJson() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            // 로깅을 남기거나 런타임 예외로 던짐
            return "{\"error\":\"Serialization failed\"}";
        }
    }

    public String toPrettyJson() {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "{\"error\":\"Serialization failed\"}";
        }
    }

}
