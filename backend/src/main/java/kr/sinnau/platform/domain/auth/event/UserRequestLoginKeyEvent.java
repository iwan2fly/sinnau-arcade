package kr.sinnau.platform.domain.auth.event;

public record UserRequestLoginKeyEvent(String email, String code) {
}
