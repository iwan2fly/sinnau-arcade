CREATE TABLE "user" (
  id              BIGSERIAL PRIMARY KEY,
  email           VARCHAR(255) NOT NULL UNIQUE,
  nickname        VARCHAR(64) NOT NULL UNIQUE,
  user_status     VARCHAR(16) NOT NULL,
  profile_image_url VARCHAR(256),
  terms_version   VARCHAR(20) NOT NULL,
  terms_agreed_at TIMESTAMPTZ NOT NULL,
  last_login_at   TIMESTAMPTZ,
  create_user_id  VARCHAR(100),
  update_user_id  VARCHAR(100),
  created_at      TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_created_at ON "user"(created_at);

