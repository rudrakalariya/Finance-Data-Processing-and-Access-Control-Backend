# Finance Data Processing API

A robust Spring Boot 3.x REST API built for a finance dashboard system. It enforces strict Role-Based Access Control (RBAC) via JWT, utilizes Soft Deletes for data integrity, provides dashboard aggregations fully calculated at the persistence (PostgreSQL/H2) level, rate limits endpoints from abuse, and conforms to RFC 7807 via structured exception handlers.

## Architecture & Choices
- **Spring Boot 3.x:** Used as the modern application skeleton bringing deep ecosystem integrations.
- **Java 21:** Takes advantage of newer JVM optimizations.
- **Spring Security + JWT:** Implements stateless authentication and strict method-level `@PreAuthorize` bindings.
- **H2 with PostgreSQL Dialect:** A zero-config in-memory persistence layer simulating true production (Postgres sequence tracking and strict null ordering). Perfect for immediate local testing.
- **Bucket4j:** Placed over Authentication API paths to slow brute force testing and protect endpoint throughput.
- **Hibernate `@SQLDelete` & `@SQLRestriction`:** Cleanest approach to implement the Soft Delete specification. Queries cleanly execute logical deletes without muddying Spring Data method logic.

## Usage & Execution

Ensure you have JDK 21 installed on your system.

### Running with Maven
```bash
./mvnw clean spring-boot:run
# OR
mvn clean spring-boot:run
```
The application will boot up on `http://localhost:8080`.

### Database
H2 Console is active.
- **URL:** `http://localhost:8080/h2-console`
- **JDBC URL:** `jdbc:h2:mem:finance_db;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH`
- **Username:** `sa`
- **Password:** `password`

### API Documentation (Swagger)
You can explore and test all APIs openly at: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Test Credentials (RBAC Pre-loaded)
The application self-seeds 4 users into the database upon startup. The universal password for all users is `password`.

1. **Admin**
   - Username: `admin` | Password: `password`
   - Access: Everything.
2. **Analyst**
   - Username: `analyst` | Password: `password`
   - Access: View Dashboard Summaries, Read Financial Records (`GET`).
3. **Viewer**
   - Username: `viewer` | Password: `password`
   - Access: View Dashboard Summaries (`GET /api/dashboard/summary`) only.
4. **Inactive User**
   - Username: `inactive` | Password: `password`
   - Access: Cannot even log in. Auth returns account locked exception.
