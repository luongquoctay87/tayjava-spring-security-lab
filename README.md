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
| Security | `spring-boot-starter-security` + `spring-security-test` |
| Build | Maven Wrapper (`./mvnw`) |
| Persistence (later) | Spring Data JPA, PostgreSQL, Flyway |
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
| Branch | `lesson-19-authorization-manager` |
| Parent | `lesson-18-method-security` |
| Lesson | 19 — Custom `AuthorizationManager` |
| What changed | `EnrolledCourseAuthorizationManager` for `GET /api/courses/{courseId}/lectures`; in-memory enrollment access |

## Demo users

| Username | Password | Role + sample authorities |
|----------|----------|---------------------------|
| `an.student` | `demo1234` | `ROLE_STUDENT`, `course:read`, `enrollment:*` |
| `bao.teacher` | `demo1234` | `ROLE_INSTRUCTOR`, owns course `1` |
| `chi.teacher` | `demo1234` | `ROLE_INSTRUCTOR`, owns course `2` (ownership demos) |
| `root.admin` | `demo1234` | `ROLE_ADMIN` + all permissions incl. `course:delete` |

## Run

```bash
./mvnw spring-boot:run
```

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
| 26 | `lesson-26-auth-entry-point` |
| 42 | `lesson-42-capstone` |
