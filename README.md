# Demo: WebFlux Security (lesson 33)

**Kind:** `demo` (side branch — do **not** merge into the Auth API project line)

| | |
|---|---|
| Branch | `demo/lesson-33-webflux` |
| Parent | `master` |
| Package | `com.tayjava.demo` |
| Entry | `com.tayjava.TayJavApplication` |
| Stack | WebFlux (Netty) — **no** `spring-boot-starter-webmvc` |

## What changed

Reactive Security twin of the servlet stack:

- `pom.xml` — `webflux` + `security-oauth2-resource-server` (webmvc removed)
- `ReactiveSecurityConfig` — `@EnableWebFluxSecurity` + `SecurityWebFilterChain`
- `MeController` — compares `@AuthenticationPrincipal`, `ReactiveSecurityContextHolder`, and broken `SecurityContextHolder`
- `DemoTokenPrinter` — `@Profile("dev")` prints a demo Bearer token
- Gate tests: public `200`, anonymous `401`, reactive principal OK, ThreadLocal empty

## Run / test

```bash
export TAYJAVA_JWT_SECRET="$(openssl rand -base64 32)"   # optional; lab default exists
./mvnw -q test
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
# confirm log: Netty started on port 8080
# copy DEMO_TOKEN=... then:
curl -s http://localhost:8080/api/public
curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8080/api/me/context
curl -s -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/me/annotation
curl -s -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/me/threadlocal
```
