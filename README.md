# Banking API + Frontend (Sprint 2)

This repository now includes:
- Spring Boot backend (`/src`)
- React + Tailwind frontend (`/frontend`)

The frontend is connected to backend APIs under `/api/v1`.

## Backend Highlights
- JWT access token + refresh token auth
- Idempotent transfers with `Idempotency-Key`
- Global API error contract
- Correlation IDs (`X-Correlation-Id`)
- Actuator endpoints

## Backend Run
```bash
mvn spring-boot:run
```
Backend: `http://localhost:8080`

## Frontend Run
```bash
cd frontend
npm install
npm run dev
```
Frontend: `http://localhost:5173`

By default, frontend calls:
- `http://localhost:8080/api/v1`

Override with:
```bash
VITE_API_BASE_URL=http://localhost:8080/api/v1 npm run dev
```

## API Docs
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`

## Frontend Features
- Register and login
- Load account overview
- Make transfer (with optional idempotency key)
- View recent statement entries

## Main API Endpoints (v1)
Auth:
- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/refresh`

Accounts:
- `GET /api/v1/accounts/me`
- `POST /api/v1/accounts/transfer`
- `GET /api/v1/accounts/{accountNumber}/statement?page=0&size=20`

## Next Recommended Steps
- Add frontend route protection and token refresh interceptor
- Add React Query for caching/retries
- Add E2E tests (Playwright/Cypress)
- Replace H2 with PostgreSQL + Flyway
