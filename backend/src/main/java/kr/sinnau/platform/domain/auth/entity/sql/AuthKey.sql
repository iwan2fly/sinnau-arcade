CREATE TABLE auth_key (
  id              BIGSERIAL PRIMARY KEY,
  email           VARCHAR(255) NOT NULL,
  key_string      VARCHAR(10)  NOT NULL,
  expire_at       TIMESTAMPTZ  NOT NULL,
  create_user_id  VARCHAR(100),
  update_user_id  VARCHAR(100),
  created_at      TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 성능 향상을 위한 인덱스 (이메일로 최신순 조회 시 필수)
CREATE INDEX idx_key_string_email_created_at ON auth_key (email, created_at DESC);