# DDL reference (mirrored by Flyway)

**Source of truth (capstone):** `src/main/resources/db/migration/`

| Flyway | Legacy reference |
|--------|------------------|
| `V1__rbac_and_tenant.sql` | `001_rbac_schema.sql` + `005_tenant.sql` |
| `V2__refresh_token.sql` | `003_refresh_token.sql` |
| `V3__security_audit_log.sql` | `006_audit.sql` |

`002_rbac_seed.sql` / `004_mfa_notes.sql` remain documentation only — runtime seed is `RbacDataSeeder` (BCrypt of `demo1234`).

## Lab runtime

H2 in-memory + **Flyway migrate** + JPA `ddl-auto=validate`. `./mvnw test` and `spring-boot:run` apply migrations automatically.

## Manual PostgreSQL

```bash
# Prefer Flyway against Postgres, or apply in order:
psql "$DATABASE_URL" -f ddl/001_rbac_schema.sql
psql "$DATABASE_URL" -f ddl/005_tenant.sql
# seed via app or 002 after replacing bcrypt placeholder
psql "$DATABASE_URL" -f ddl/003_refresh_token.sql
psql "$DATABASE_URL" -f ddl/006_audit.sql
```
