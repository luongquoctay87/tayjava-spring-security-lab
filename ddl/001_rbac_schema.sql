-- Lesson 20 RBAC schema (PostgreSQL-compatible). Lab runtime uses H2 + JPA ddl-auto=update.
-- Apply manually when targeting a real database: psql -f ddl/001_rbac_schema.sql

CREATE TABLE IF NOT EXISTS permission (
  id     BIGSERIAL PRIMARY KEY,
  name   VARCHAR(64) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS role (
  id     BIGSERIAL PRIMARY KEY,
  name   VARCHAR(32) NOT NULL UNIQUE  -- STUDENT / INSTRUCTOR / ADMIN — no ROLE_ prefix
);

CREATE TABLE IF NOT EXISTS role_permission (
  role_id       BIGINT NOT NULL REFERENCES role(id),
  permission_id BIGINT NOT NULL REFERENCES permission(id),
  PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE IF NOT EXISTS app_user (
  id       BIGSERIAL PRIMARY KEY,
  username VARCHAR(64)  NOT NULL UNIQUE,
  password VARCHAR(100) NOT NULL,
  email    VARCHAR(120) NOT NULL,
  enabled  BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS user_role (
  user_id BIGINT NOT NULL REFERENCES app_user(id),
  role_id BIGINT NOT NULL REFERENCES role(id),
  PRIMARY KEY (user_id, role_id)
);
