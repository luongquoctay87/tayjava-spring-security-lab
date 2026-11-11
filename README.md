# Demo: JWT structure (lesson 22)

**Kind:** `demo` (side branch — do **not** merge into the Auth API project line)

| | |
|---|---|
| Branch | `demo/lesson-22-jwt-structure` |
| Parent | `master` |
| Package | `com.tayjava.demo` |
| Entry | `com.tayjava.TayJavApplication` |

## What changed

JWT sign/verify playground outside the project Auth API line:

- `JwtConfig` — HS256 `JwtEncoder` / `JwtDecoder` (algorithm pinned)
- `JwtDemo` — throwaway issuer (`issueForDemo`) — not `AccessTokenService`
- `TokenPlaygroundController` — `GET /api/debug/token`
- `DemoSecurityConfig` — permits the debug endpoint
- Gate tests: valid decode, signature tamper, payload tamper, `alg: none` rejected

## Run / test

```bash
export TAYJAVA_JWT_SECRET="$(openssl rand -base64 32)"
./mvnw -q test
./mvnw spring-boot:run

TOKEN=$(curl -s http://localhost:8080/api/debug/token | sed -E 's/.*"token":"([^"]+)".*/\1/')
echo "$TOKEN"
```

Do not commit real secrets. `src/test/resources/application.yml` holds a lab-only test secret.
