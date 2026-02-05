# Banking API + Frontend (Sprint 2.1)

This repository includes:
- Spring Boot backend (`/src`)
- React + Tailwind frontend (`/frontend`)

Frontend now has a modular structure with route guards, auth context, and API error mapping.

## Backend Highlights
- JWT access token + refresh token auth
- Idempotent transfers with `Idempotency-Key`
- Global API error contract (`message`, `traceId`, etc.)
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

## Frontend Architecture
- `src/auth` → auth context + protected route
- `src/api` → central HTTP client with refresh-token retry flow
- `src/pages` → Login/Register/Dashboard pages
- `src/hooks` → dashboard data loader hook
- `src/components` → app shell/cards/error banner

## UX Improvements
- Route protection (`/dashboard` requires login)
- Auto refresh-token retry on 401
- Friendly error banner with backend `traceId` for support/debug
- Session persistence in local storage

## Main API Endpoints (v1)
Auth:
- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/refresh`

Accounts:
- `GET /api/v1/accounts/me`
- `POST /api/v1/accounts/transfer`
- `GET /api/v1/accounts/{accountNumber}/statement?page=0&size=20`
