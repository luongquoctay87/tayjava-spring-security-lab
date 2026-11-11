-- Lesson 34: shared-schema multi-tenant column
ALTER TABLE app_user ADD COLUMN IF NOT EXISTS tenant_id VARCHAR(64) NOT NULL DEFAULT 'acme';
CREATE INDEX IF NOT EXISTS idx_app_user_tenant ON app_user (tenant_id);
