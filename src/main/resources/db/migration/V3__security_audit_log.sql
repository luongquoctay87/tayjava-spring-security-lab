CREATE TABLE security_audit_log (
  id         BIGSERIAL PRIMARY KEY,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  event_type VARCHAR(64) NOT NULL,
  principal  VARCHAR(128),
  outcome    VARCHAR(32) NOT NULL,
  reason     VARCHAR(128),
  client_ip  VARCHAR(64),
  tenant_id  VARCHAR(64),
  extra      VARCHAR(4000)
);
