# Real-World Banking API (Spring Boot)

A more production-style banking backend with:
- user registration and login
- auto-generated **Customer ID** and **Account Number**
- token-based authentication
- account lifecycle controls
- beneficiaries and beneficiary transfer
- statement filters and transaction references
- Swagger UI + H2 in-memory database

## Stack
- Java 17
- Spring Boot 3
- Spring Web, Validation
- Spring Data JPA + H2
- springdoc OpenAPI (Swagger UI)
- BCrypt password hashing

## Run
```bash
mvn spring-boot:run
```

## Docs and Tools
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`
- H2 Console: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:bankdb`
  - User: `sa`
  - Password: *(empty)*

## Auth Flow
### 1) Register
`POST /api/auth/register`

Example body:
```json
{
  "fullName": "Alice Doe",
  "email": "alice@example.com",
  "username": "alice01",
  "password": "Password@123",
  "phoneNumber": "+12345678901",
  "accountType": "SAVINGS",
  "initialDeposit": 1000.00
}
```

Returns token + generated customer and primary account details.

### 2) Login
`POST /api/auth/login`

Use returned token in protected APIs:

`Authorization: Bearer <token>`

## Core Banking APIs
Base path: `/api/accounts`

### My Accounts & Profile
- `GET /api/accounts/me`

### Open Additional Account
- `POST /api/accounts`

Body:
```json
{
  "accountType": "CURRENT",
  "initialDeposit": 500.00
}
```

### Deposit / Withdraw
- `POST /api/accounts/{accountNumber}/deposit`
- `POST /api/accounts/{accountNumber}/withdraw`

### Internal Transfer
- `POST /api/accounts/transfer`

### Beneficiaries
- `POST /api/accounts/{accountNumber}/beneficiaries`
- `GET /api/accounts/{accountNumber}/beneficiaries`
- `POST /api/accounts/{accountNumber}/beneficiaries/{beneficiaryAccountNumber}/transfer`

### Statements
- `GET /api/accounts/{accountNumber}/statement`
- `GET /api/accounts/{accountNumber}/statement?from=2026-01-01T00:00:00&to=2026-12-31T23:59:59`

### Account Controls
- `PATCH /api/accounts/{accountNumber}/status?status=FROZEN`
  - Supported: `ACTIVE`, `FROZEN`, `CLOSED`

### Public Search (demo)
- `GET /api/accounts/search?name=alice`

## Notes
- IDs are sequentially generated:
  - customer: `CUST000001`
  - account: `AC000001`
- Passwords are stored as BCrypt hashes.
- Authentication tokens expire in 12 hours.
- This is still a demo project; for production, move to JWT/refresh tokens, add RBAC, auditing, rate limits, and robust exception handling.
