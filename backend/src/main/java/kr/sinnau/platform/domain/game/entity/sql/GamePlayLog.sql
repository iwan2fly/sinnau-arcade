CREATE TABLE game_play_log (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    game_id VARCHAR(50) NOT NULL,
    result VARCHAR(20) NOT NULL,
    spent_amount BIGINT DEFAULT 0,
    earned_amount BIGINT DEFAULT 0,
    score DOUBLE PRECISION,
    create_user_id VARCHAR(50),
    update_user_id VARCHAR(50),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_game_play_log_user_id ON game_play_log(user_id);
CREATE INDEX idx_game_play_log_game_id ON game_play_log(game_id);
CREATE INDEX idx_game_play_log_created_at ON game_play_log(created_at);
