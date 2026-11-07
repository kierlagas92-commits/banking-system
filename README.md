# Banking System

A RESTful Banking System built with Java and Spring Boot.

The application supports account management, deposits, withdrawals, money transfers, transaction management, and transfer auditing.

## Technologies Used

- Java
- Spring Boot
- Spring JDBC
- MySQL
- Maven
- JUnit 5
- Mockito
- Git and GitHub
- Postman

## Features

- Create and retrieve bank accounts
- Deposit money
- Withdraw money
- Transfer money between accounts
- Validate account operations
- Prevent transfers between the same account
- Prevent insufficient-balance withdrawals and transfers
- Manage database transactions using Spring's `@Transactional`
- Support ACID transaction properties
- Roll back failed transfers
- Record successful and failed transfer audit records
- Handle API exceptions with centralized exception handling
- Return appropriate HTTP status codes
- Unit and integration testing

## Project Architecture

The application follows a layered architecture:

- **Controller Layer** – Handles incoming HTTP requests and returns API responses.
- **Service Layer** – Contains business logic and transaction management.
- **Repository Layer** – Communicates with the MySQL database using Spring JDBC.
- **Model Layer** – Represents the application's domain objects.
- **DTO Layer** – Transfers request data between the client and application.
- **Exception Layer** – Handles application exceptions and creates structured API error responses.

## Database Setup

### 1. Create the database

Open MySQL Workbench and execute:

```sql
CREATE DATABASE banking_system;

USE banking_system;
```

### 2. Create the accounts table

```sql
CREATE TABLE accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_number BIGINT NOT NULL UNIQUE,
    account_holder VARCHAR(100) NOT NULL,
    balance DECIMAL(19, 2) NOT NULL DEFAULT 0.00,
    CONSTRAINT chk_account_number_positive
        CHECK (account_number > 0),
    CONSTRAINT chk_balance_nonnegative
        CHECK (balance >= 0)
);
```

### 3. Create the transfer_audit table

```sql
CREATE TABLE transfer_audit (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    from_account BIGINT NOT NULL,
    to_account BIGINT NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    message VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 4. Configure the database connection

Update `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/banking_system
spring.datasource.username=root
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

### 5. Configure the environment variable

Before running the application, set your MySQL password.

**Windows PowerShell:**

```powershell
$env:DB_PASSWORD = "your_mysql_password"
```

Replace `your_mysql_password` with your actual MySQL password.

**Important:** Never commit your actual database password or other secrets to GitHub.

## API Endpoints

Base URL: `http://localhost:8080/api/accounts`

| HTTP Method | Endpoint | Description |
|---|---|---|
| GET | `/ {accountNumber}` | Retrieve an account |
| POST | `/` | Create a new account |
| POST | `/{accountNumber}/deposit` | Deposit money |
| POST | `/{accountNumber}/withdraw` | Withdraw money |
| POST | `/transfer` | Transfer money between accounts |

### Request Examples

**Create an account**

`POST /api/accounts`

```json
{
  "accountNumber": 10007,
  "accountHolder": "Michael",
  "initialBalance": 1000.00
}
```

**Deposit money**

`POST /api/accounts/10007/deposit`

```json
{
  "amount": 250.00
}
```

**Withdraw money**

`POST /api/accounts/10007/withdraw`

```json
{
  "amount": 100.00
}
```

**Transfer money**

`POST /api/accounts/transfer`

```json
{
  "fromAccountNumber": 10007,
  "toAccountNumber": 10001,
  "amount": 200.00
}
```

## Running the Application

### 1. Clone the repository

```bash
git clone https://github.com/kierlagas92-commits/banking-system.git
cd banking-system
```

### 2. Configure the database password

In Windows PowerShell:

```powershell
$env:DB_PASSWORD = "your_mysql_password"
```

### 3. Run the application

```bash
mvn spring-boot:run
```

The application will start at:

`http://localhost:8080`

## Running Tests

Execute all unit and integration tests:

```bash
mvn clean test
```

The tests verify business logic, database operations, transaction rollback, and transfer auditing.