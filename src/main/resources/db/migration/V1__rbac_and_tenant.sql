-- Capstone: RBAC schema with tenant_id (Flyway is the source of truth)
CREATE TABLE permission (
  id   BIGSERIAL PRIMARY KEY,
  name VARCHAR(64) NOT NULL UNIQUE
);

CREATE TABLE role (
  id   BIGSERIAL PRIMARY KEY,
  name VARCHAR(32) NOT NULL UNIQUE
);

CREATE TABLE role_permission (
  role_id       BIGINT NOT NULL REFERENCES role(id),
  permission_id BIGINT NOT NULL REFERENCES permission(id),
  PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE app_user (
  id        BIGSERIAL PRIMARY KEY,
  username  VARCHAR(64)  NOT NULL UNIQUE,
  password  VARCHAR(100) NOT NULL,
  email     VARCHAR(120) NOT NULL,
  enabled   BOOLEAN      NOT NULL DEFAULT TRUE,
  tenant_id VARCHAR(64)  NOT NULL DEFAULT 'acme'
);

CREATE INDEX idx_app_user_tenant ON app_user (tenant_id);

CREATE TABLE user_role (
  user_id BIGINT NOT NULL REFERENCES app_user(id),
  role_id BIGINT NOT NULL REFERENCES role(id),
  PRIMARY KEY (user_id, role_id)
);
