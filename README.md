# Banking Application API (Spring Boot)

A Spring Boot REST API for simple banking operations with:
- **Swagger UI** documentation
- **H2 in-memory database**
- Layered architecture (controller/service/repository)

## Tech Stack
- Java 17
- Spring Boot 3
- Spring Web
- Spring Data JPA
- H2 Database
- springdoc OpenAPI (Swagger UI)

## Run the app
```bash
mvn spring-boot:run
```

The app starts on `http://localhost:8080`.

## Useful URLs
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`
- H2 Console: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:bankdb`
  - User: `sa`
  - Password: *(empty)*

## API Endpoints
Base path: `/api/accounts`

- `POST /api/accounts` - Open account
- `POST /api/accounts/{accountNumber}/deposit` - Deposit
- `POST /api/accounts/{accountNumber}/withdraw` - Withdraw
- `POST /api/accounts/transfer` - Transfer
- `GET /api/accounts` - List accounts
- `GET /api/accounts/search?name=...` - Search by customer name
- `GET /api/accounts/{accountNumber}/statement` - Account statement

## Sample Request
### Open Account
```json
{
  "name": "Alice",
  "email": "alice@example.com",
  "accountType": "SAVINGS",
  "initialDeposit": 1500.00
}
```
