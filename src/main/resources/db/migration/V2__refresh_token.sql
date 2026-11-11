CREATE TABLE refresh_token (
  id               BIGSERIAL PRIMARY KEY,
  user_id          BIGINT       NOT NULL REFERENCES app_user(id),
  token_hash       VARCHAR(64)  NOT NULL UNIQUE,
  family_id        VARCHAR(36)  NOT NULL,
  expires_at       TIMESTAMP WITH TIME ZONE NOT NULL,
  revoked_at       TIMESTAMP WITH TIME ZONE,
  replaced_by_hash VARCHAR(64),
  created_at       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_refresh_token_family ON refresh_token(family_id);
CREATE INDEX idx_refresh_token_user ON refresh_token(user_id);
