# TayJava Spring Security Lab

Sample Spring Boot + Spring Security application that grows with the **TayJava Spring Security** article series.

Readers build one continuous app while learning Spring Security 7 on Boot 4: from the first secured app, through form login / session / CSRF, RBAC, JWT APIs, and a production checklist. Each lesson that changes the code has a matching **git branch**.

This repository is a companion for learning — not the TayJava production backend.

## What you will build

- Layered monolith under package `com.tayjava` (`controller` / `service` / `entity` / `common` / `config.security`)
- AuthN/Z around Course APIs and admin surfaces (`STUDENT` / `INSTRUCTOR` / `ADMIN`)
- Form login + session, then stateless JWT (`/api/auth/login`, refresh), later OAuth2 / Resource Server lessons
- Centralized errors (`ErrorException` / `Resp`) aligned with `e-learning`

## Tech stack

| Area | Choice |
|------|--------|
| Language | Java 21 |
| Framework | Spring Boot 4.0.9 + Spring Security 7.x |
| Web | Spring MVC (`spring-boot-starter-webmvc`) |
| Security | `spring-boot-starter-security` + `spring-boot-starter-security-oauth2-resource-server` + `spring-security-test` |
| Build | Maven Wrapper (`./mvnw`) |
| Persistence | Spring Data JPA + H2 (lab); `ddl/` for PostgreSQL reference |
| Tests | JUnit 5, MockMvc / `@WithMockUser`, Testcontainers (later) |

## Project coordinates

| | |
|---|---|
| Group / artifact | `com.tayjava` / `tayjava-spring-security-lab` |
| Main class | `com.tayjava.TayJavApplication` |
| Default branch | `master` — lesson 04 skeleton |
| Port | `8080` |
| Demo user (master) | `an.student` / `demo1234` |
| License | MIT |
| Branch map | `blogs/java/spring-security/lab-samples-map.md` (TayJava blogs workspace) |

## This branch

| | |
|---|---|
| Branch | `lesson-23-jwt-login` |
| Parent | `lesson-21-stateless` |
| Lesson | 23 — Login API that returns JWT |
| What changed | `POST /api/auth/login` → HS256 access token via `AuthenticationManager` + `JwtEncoder`; secret from `TAYJAVA_JWT_SECRET` |

## Demo users

| Username | Password | Role + sample authorities |
|----------|----------|---------------------------|
| `an.student` | `demo1234` | `ROLE_STUDENT`, `course:read`, `enrollment:*` |
| `bao.teacher` | `demo1234` | `ROLE_INSTRUCTOR`, owns course `1` |
| `chi.teacher` | `demo1234` | `ROLE_INSTRUCTOR`, owns course `2` (ownership demos) |
| `root.admin` | `demo1234` | `ROLE_ADMIN` + all permissions incl. `course:delete` |

## Run

```bash
export TAYJAVA_JWT_SECRET="$(openssl rand -base64 32)"
./mvnw spring-boot:run
# RBAC seed runs on empty DB (an.student / bao.teacher / chi.teacher / root.admin)

curl -s -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"an.student","password":"demo1234"}'
```

See `.env.example` for the env var name. Never commit a real secret.

## DDL

See `ddl/README.md`. Runtime uses H2 + `RbacDataSeeder`; SQL files are the PostgreSQL reference.

## Test

```bash
./mvnw -q test
```

## Lesson → branch (project line)

See the full Parent→Branch map in `blogs/java/spring-security/lab-samples-map.md`. Highlights:

| Lesson | Branch |
|--------|--------|
| 04 | `master` |
| 06 | `lesson-06-security-filter-chain` |
| 16 | `lesson-16-authorize-http` |
| 20 | `lesson-20-rbac` |
| 23 | `lesson-23-jwt-login` |
| 26 | `lesson-26-auth-entry-point` |
| 42 | `lesson-42-capstone` |
