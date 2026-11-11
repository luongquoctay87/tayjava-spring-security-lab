-- Lesson 25: refresh tokens (PostgreSQL-compatible). Lab runtime uses H2 + JPA ddl-auto=update.

CREATE TABLE IF NOT EXISTS refresh_token (
  id                BIGSERIAL PRIMARY KEY,
  user_id           BIGINT       NOT NULL REFERENCES app_user(id),
  token_hash        VARCHAR(64)  NOT NULL UNIQUE,
  family_id         VARCHAR(36)  NOT NULL,
  expires_at        TIMESTAMPTZ  NOT NULL,
  revoked_at        TIMESTAMPTZ,
  replaced_by_hash  VARCHAR(64),
  created_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_refresh_token_family ON refresh_token(family_id);
CREATE INDEX IF NOT EXISTS idx_refresh_token_user ON refresh_token(user_id);
