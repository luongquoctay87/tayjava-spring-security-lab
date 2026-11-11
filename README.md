# Demo: Spring Authorization Server (lesson 30)

**Kind:** `demo` (side branch — do **not** merge into the Auth API project line)

| | |
|---|---|
| Branch | `demo/lesson-30-authorization-server` |
| Parent | `master` |
| Package | `com.tayjava.demo` |
| Entry | `com.tayjava.TayJavApplication` |
| Port | `9000` |

## What changed

Standalone Authorization Server (SAS 2.x / Boot 4) for OAuth2/OIDC exercises:

- `AuthorizationServerConfig` — protocol chain + form-login chain
- `RegisteredClientConfig` — `tayjava-web` (PKCE), `tayjava-mobile` (public), `tayjava-partner-sync` (client_credentials)
- `JwkConfig` — lab ephemeral RSA (`kid=tayjava-lab-demo`) + issuer `http://localhost:9000`
- `DemoUserConfig` — `an.student` / `bao.teacher` (`demo1234`)
- Gate tests: OpenID discovery, client_credentials happy path + wrong secret `401`

## Run / test

```bash
./mvnw -q test
./mvnw spring-boot:run

curl -s http://localhost:9000/.well-known/openid-configuration | head
curl -s -u tayjava-partner-sync:lab-partner-secret \
  -d grant_type=client_credentials -d scope=enrollments.read \
  http://localhost:9000/oauth2/token
```

Lab secrets default to `lab-web-secret` / `lab-partner-secret` (override with `TAYJAVA_WEB_CLIENT_SECRET` / `TAYJAVA_PARTNER_CLIENT_SECRET`). Ephemeral JWK is for local learning only — production loads a stable keystore.
