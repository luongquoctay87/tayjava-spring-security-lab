# lesson-23-jwt-login

- **Parent:** `lesson-21-stateless`
- **What changed:** `POST /api/auth/login` authenticates via `AuthenticationManager` and returns an HS256 JWT (`TokenResp`); `JwtEncoder`/`JwtDecoder` + `tayjava.jwt.*` (secret from `TAYJAVA_JWT_SECRET`)

Run: `export TAYJAVA_JWT_SECRET="$(openssl rand -base64 32)" && ./mvnw -q test`
