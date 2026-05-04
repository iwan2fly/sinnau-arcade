CREATE TABLE coin_history (
  id              BIGSERIAL PRIMARY KEY,
  user_id         BIGINT NOT NULL,
  amount          BIGINT NOT NULL,
  type            VARCHAR(10) NOT NULL, -- EARN, SPEND
  reason          VARCHAR(50) NOT NULL, -- GAME_WIN, GAME_BET
  create_user_id  VARCHAR(100),
  update_user_id  VARCHAR(100),
  created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_coin_history_user_id ON coin_history(user_id);
