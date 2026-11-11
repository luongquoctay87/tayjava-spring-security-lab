# Demo: Keycloak SSO (lesson 31)

**Kind:** `demo` (side branch — do **not** merge into the Auth API project line)

| | |
|---|---|
| Branch | `demo/lesson-31-keycloak` |
| Parent | `master` |
| Package | `com.tayjava.demo` |
| Entry | `com.tayjava.TayJavApplication` |

## What changed

Keycloak compose/realm demo + Spring resource-server role mapping:

- `docker-compose.yml` — Keycloak 26.4 on `:8180` (`start-dev --import-realm`)
- `keycloak/import/tayjava-realm.json` — realm `tayjava`, clients `tayjava-web` / `tayjava-api`, users `an.student` / `bao.teacher`
- `KeycloakJwtAuthenticationConverter` — `realm_access` + `resource_access` → `ROLE_*`
- `DemoSecurityConfig` + `DemoApiController` — sample `/api/public`, `/api/me`, `/api/admin/ping`
- Gate tests: converter mapping, anonymous `401`, student `403` on admin, admin `200`

Default profile uses a local HS256 `JwtDecoder` so `./mvnw test` works **without** Docker. Profile `keycloak` documents `issuer-uri` for a live IdP.

## Run / test

```bash
./mvnw -q test

# Optional live Keycloak
docker compose up -d
# Admin console: http://localhost:8180  (admin / admin — local only)
```

Realm users: `an.student` / `bao.teacher` — password `demo1234`.
Web client secret in import file: `lab-keycloak-web-secret` (lab only).
