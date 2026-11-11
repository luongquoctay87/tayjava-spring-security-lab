# Demo: Filter chain (lesson 05)

**Kind:** `demo` (side branch — do **not** merge into the Auth API project line)

| | |
|---|---|
| Branch | `demo/lesson-05-filter-chain` |
| Parent | `master` |
| Package | `com.tayjava.demo.filter` (+ `config.security` / `controller`) |
| Entry | `com.tayjava.TayJavApplication` |

## What changed

Filter-chain probe on a side branch (not part of the Auth API project line):

- `FilterChainProbeFilter` — servlet `OncePerRequestFilter` that marks traffic hitting the filter chain
- `ProbeController` — `GET /api/probe`
- `SecurityConfig` — permits `/api/probe` for anonymous inspection
- Gate tests: probe endpoint reachable and filter hit flag set

## Run / test

```bash
./mvnw -q test
./mvnw spring-boot:run
curl -i http://localhost:8080/api/probe
```
