-- Lesson 41: append-only audit log sketch (lab uses InMemoryAuditEventRepository)
CREATE TABLE IF NOT EXISTS security_audit_log (
  id          BIGSERIAL PRIMARY KEY,
  created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  event_type  VARCHAR(64) NOT NULL,
  principal   VARCHAR(128),
  outcome     VARCHAR(32) NOT NULL,
  reason      VARCHAR(128),
  client_ip   VARCHAR(64),
  tenant_id   VARCHAR(64),
  extra       JSONB
);
-- Production: GRANT INSERT only; REVOKE UPDATE, DELETE FROM app role.
