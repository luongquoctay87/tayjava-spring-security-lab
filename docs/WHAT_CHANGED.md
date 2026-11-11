# lesson-29-resource-server

- **Parent:** `lesson-28-oauth2-login`
- **What changed:** Clarify Resource Server role: default remains self-issued HS256; profile `external-jwt` validates via `issuer-uri`/JWKS; profile `opaque` documents introspection placeholders; audience validator covered by tests

Run: `./mvnw -q test`
