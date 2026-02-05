# Banking API (Production-Readiness Upgrade)

This project is now a stronger backend baseline with:
- JWT access tokens + refresh tokens
- Spring Security filter chain (stateless)
- idempotent transfer support (`Idempotency-Key`)
- structured API error response + global exception handling
- observability basics (Actuator + correlation IDs)
- versioned endpoints (`/api/v1/...`)
- paginated statements

## Stack
- Java 17
- Spring Boot 3
- Spring Web, Validation, Security, Actuator
- Spring Data JPA + H2 (dev)
- springdoc OpenAPI / Swagger UI
- JJWT for token handling

## Run
```bash
mvn spring-boot:run
```

## URLs
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`
- H2 Console: `http://localhost:8080/h2-console`
- Health: `http://localhost:8080/actuator/health`

## Auth API (v1)
Base: `/api/v1/auth`

- `POST /register`
- `POST /login`
- `POST /refresh`

`login/register` response now returns:
- `accessToken` (JWT)
- `refreshToken`
- `tokenType`
- `expiresInSeconds`

Use access token in requests:

`Authorization: Bearer <accessToken>`

## Accounts API (v1)
Base: `/api/v1/accounts`

- `GET /me`
- `POST /`
- `POST /{accountNumber}/deposit`
- `POST /{accountNumber}/withdraw`
- `POST /transfer` *(supports `Idempotency-Key` header)*
- `POST /{accountNumber}/beneficiaries/{beneficiaryAccountNumber}/transfer` *(supports `Idempotency-Key` header)*
- `GET /{accountNumber}/statement?page=0&size=20`
- `GET /{accountNumber}/statement?from=...&to=...&page=0&size=20`
- `PATCH /{accountNumber}/status?status=FROZEN`

## Observability and Error Contract
- Every response includes `X-Correlation-Id` (generated if absent in request).
- Validation/business errors follow a consistent JSON shape from global exception handling:
  - `timestamp`, `status`, `error`, `message`, `path`, `traceId`

## Next steps for full production
- Replace H2 with PostgreSQL + Flyway
- Add RBAC and fine-grained authorization
- Add rate limiting and 2FA for sensitive operations
- Add CI/CD quality gates, security scans, load tests
