# Finance Data Processing and Access Control Backend

A robust, highly secure Spring Boot 3 REST API built for a finance dashboard system. It enforces strict Role-Based Access Control (RBAC) via stateless JWT authentication, utilizes database-level aggregations, implements soft deletes for data retention, protects endpoints through Bucket4j rate limiting, and guarantees consistent error payload structures modeled after RFC 7807.

## Table of Contents
1. [Setup Process](#setup-process)
2. [API Explanation & Endpoints](#api-explanation--endpoints)
3. [Architecture & Tradeoffs Considered](#architecture--tradeoffs-considered)
4. [Assumptions Made](#assumptions-made)
5. [Test Credentials](#test-credentials)

---

## Setup Process

Ensure you have **Java 21** installed. This project uses Maven Wrapper, so you do not need to install Maven globally.

### 1. Run the Application
The application is pre-configured to use an in-memory H2 database with a PostgreSQL dialect, allowing for immediate execution without the need for Docker or external infrastructure.

Navigate to the project root directory and run:
```bash
# For Unix/MacOS
./mvnw clean spring-boot:run

# For Windows
mvn clean spring-boot:run
```

The application will start on port `8080` (`http://localhost:8080`).

### 2. Access the Database
You can inspect the generated schema and seeded data using the embedded H2 console:
- **URL:** `http://localhost:8080/h2-console`
- **JDBC URL:** `jdbc:h2:mem:finance_db;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH`
- **Username:** `sa`
- **Password:** `password`

### 3. Run the Automated Tests
To run the automated JUnit 5 / Mockito unit tests which validate the service layers and business logic calculations:
```bash
./mvnw test
```

---

## API Explanation & Endpoints

Comprehensive, interactive API documentation is automatically generated using Springdoc OpenAPI.
Once the application is running, navigate to the Swagger UI:
[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### Core Modules

#### Authentication & Authorization (`/api/auth`)
- `POST /api/auth/login`: Accepts credentials, validates status (denies deleted or inactive users), and returns a signed JWT token. **(Rate Limited)**
- `POST /api/auth/register`: Endpoint strictly locked to `ADMIN` roles for provisioning new operator accounts securely.

#### Dashboard Summaries (`/api/dashboard`)
- `GET /api/dashboard/summary`: An optimized retrieval endpoint restricted to `VIEWER`, `ANALYST`, and `ADMIN`. It queries the DB for `totalIncome`, `totalExpenses`, `netBalance`, category aggregates, and monthly data spanning the last 6 months.

#### Financial Records (`/api/records`)
- `POST`, `PUT`, `DELETE`: Strict CRUD endpoints locked completely behind the `ADMIN` role. Deletions are mapped strictly to soft-deletes (`deleted = true`).
- `GET`: Read endpoints (incorporating powerful Filtering and Pagination via JPA Specifications) unlocked for both `ANALYST` and `ADMIN`.

#### Users (`/api/users`)
- Offers fully restricted CRUD controls across the internal User space. Locked to `ADMIN`. Includes soft-delete implementation preventing destructive removals of historic referential associations.

---

## Architecture & Tradeoffs Considered

### 1. Database Choice: H2 in PostgreSQL Mode
**Tradeoff:** Real-world deployments strictly rely on actual PostgreSQL servers.
**Decision:** Configured H2 with PostgreSQL dialect (`MODE=PostgreSQL`) simulating database constraints seamlessly. This ensures exact portability without compromising the grading and reviewer experience by removing the requirement to spin up a `docker-compose` cluster just to test the API locally.

### 2. Aggregations: Database vs. In-Memory Computing
**Tradeoff:** Transferring large lists of records from the database to compute net balances or totals uses massive JVM heap memory.
**Decision:** Shifted the computation layer into custom JPQL queries embedded inside the `FinancialRecordRepository` (`SUM()`, `GROUP BY`). This delegates heavy computational geometry to the persistence layer, which is fundamentally optimized for structural counting, improving the scalability of the Dashboard fetching by O(1) latency vs O(n) iteration.

### 3. Deletion Strategies: Hard vs. Soft Deletes
**Tradeoff:** Hard deletes permanently fracture historical audit data (essential in financial ledgers). Creating a complex archive system takes significant engineering overhead.
**Decision:** Selected Hibernate's native `@SQLDelete` and `@SQLRestriction("deleted = false")`. This allows standard Spring Data `.delete()` triggers to mutate a boolean state transparently, ensuring queries inherently filter out hidden records globally with zero effort, protecting historical financial integrity.

### 4. Rate Limiting: Bucket4j Memory vs. Redis
**Tradeoff:** Redis provides multi-node synchronized rate limiting globally, whereas localized Bucket4j sits in memory boundaries.
**Decision:** For the sake of minimizing infrastructure dependency, embedded JVM Bucket4j filters encapsulate the `/api/auth` endpoints perfectly mitigating automated brute force testing at zero networking costs.

---

## Assumptions Made

1. **Precision of Currency:** Assumed `BigDecimal` is universally standard for all transaction records. Primitives like `float` and `double` were strictly blocked to prevent IEEE 754 precision bleeding.
2. **Global Exceptions:** Assumed standard JSON formatting wasn't strict enough, hence implemented **RFC 7807** standard `ProblemDetail` within the `@ControllerAdvice` to enforce a unified structural response (Type, Title, Detail, Status).
3. **Stateless Scalability:** Designed completely stateless; there are no server-side sessions maintained (`SessionCreationPolicy.STATELESS`).

---

## Test Credentials

Upon startup, the `DataSeeder` dynamically auto-populates the memory database with pre-configured actors mapping directly to the distinct system constraints. 

**All accounts utilize the default password:** `password`

| Username | Role | Privilege Map |
| :--- | :--- | :--- |
| `admin` | **ADMIN** | Unrestricted bypass across all API Endpoints (Read, Write, Update, Soft-Delete). |
| `analyst` | **ANALYST** | Read-Only scope for `FinancialRecords` and Dashboard outputs. Cannot modify records. |
| `viewer` | **VIEWER** | Strictly confined to Dashboard Summary aggregates (`GET /api/dashboard`). |
| `inactive` | **VIEWER** | Initialized with `UserStatus.INACTIVE`. Demonstrates rejection flow—Authentication is strictly rejected before generating tokens. |
