# Capstone production checklist (lesson 42)

Tick before deploy. Companion code: branch `lesson-42-capstone`.

## Authentication
- [ ] Password login + refresh rotation work; replay revokes family
- [ ] MFA/OTP required for INSTRUCTOR/ADMIN API login
- [ ] Soft-lock after N username failures; IP rate limit returns 429
- [ ] No username enumeration via distinct error messages/timing

## Authorization
- [ ] Neo endpoints: GET `/api/courses` public; POST create INSTRUCTOR/ADMIN 201; GET `/api/enrollments/me` auth
- [ ] Method security + `hasPermission` on course WRITE
- [ ] Multi-tenant: JWT `tenant_id` enforced on mutations
- [ ] Dual chains: `/admin/**` form+session; `/api/**` Bearer/STATELESS

## Transport / browser
- [ ] CORS via single `CorsConfigurationSource` + `http.cors(withDefaults())`
- [ ] CSP / frame options headers present
- [ ] CSRF disabled only on stateless API (documented)

## Testing / audit
- [ ] Role×endpoint matrix tests in CI
- [ ] JWT/OAuth MockMvc tests without real IdP secrets
- [ ] Audit events recorded; no passwords/tokens/OTP in audit store
- [ ] Actuator exposure limited (`health`/`info`)

## Data
- [ ] Flyway migrations are schema source of truth; `ddl-auto=validate`
- [ ] Demo credentials never shipped as production secrets
