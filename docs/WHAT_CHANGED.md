# lesson-24-jwt-filter

- **Parent:** `lesson-23-jwt-login`
- **What changed:** Wire `oauth2ResourceServer().jwt()` on `/api/**` (`BearerTokenAuthenticationFilter`); map claim `roles` + `permissions` via `JwtAuthenticationConverter`; drop API `httpBasic`

Run: `./mvnw -q test`
