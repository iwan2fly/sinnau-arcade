CREATE TABLE nickname_history (
  id              BIGSERIAL PRIMARY KEY,
  user_id         BIGINT NOT NULL,
  before_nickname VARCHAR(64) NOT NULL,
  after_nickname  VARCHAR(64) NOT NULL,
  create_user_id  VARCHAR(100),
  update_user_id  VARCHAR(100),
  created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_nickname_history_user_id ON nickname_history(user_id);
