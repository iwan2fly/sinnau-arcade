CREATE TABLE user_role (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_code VARCHAR(32) NOT NULL, -- 'USER', 'ADMIN' 등
    create_user_id  VARCHAR(100),
    update_user_id  VARCHAR(100),
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_role UNIQUE (user_id, role_code)
);

CREATE INDEX idx_user_role_user_id ON user_role(user_id);