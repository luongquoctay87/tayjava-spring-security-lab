# DDL — RBAC + refresh tokens

| File | Purpose |
|------|---------|
| `001_rbac_schema.sql` | `app_user` / `role` / `permission` + join tables |
| `002_rbac_seed.sql` | Reference seed for `an.student`, `bao.teacher`, `chi.teacher`, `root.admin` |
| `003_refresh_token.sql` | `refresh_token` table (hash, family, revoke columns) |
| `004_mfa_notes.sql` | MFA column sketch (lab uses in-memory tickets + fixed lab OTP) |
| `005_tenant.sql` | `app_user.tenant_id` shared-schema isolation |
| `006_audit.sql` | `security_audit_log` append-only sketch |

## Lab runtime

The companion app uses **H2 in-memory** + JPA `ddl-auto=update` and seeds users through `RbacDataSeeder` (BCrypt of `demo1234` at startup). You do **not** need to apply these scripts for `./mvnw test` or `./mvnw spring-boot:run`.

## Manual PostgreSQL apply

```bash
psql "$DATABASE_URL" -f ddl/001_rbac_schema.sql
# Replace :bcrypt_demo1234 in 002_rbac_seed.sql with a real BCrypt hash, then:
psql "$DATABASE_URL" -f ddl/002_rbac_seed.sql
psql "$DATABASE_URL" -f ddl/003_refresh_token.sql
```

Flyway arrives later in the series (capstone / earlier if a delta requires it).
