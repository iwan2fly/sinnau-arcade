package kr.sinnau.platform.domain.auth.model;

public enum Role {
    GUEST,         // 이메일 인증은 했지만, 아직 약관 동의/닉네임 설정을 안 한 사용자
    USER,          // 모든 가입 절차를 완료한 정식 사용자
    ADMIN          // 관리자
}

