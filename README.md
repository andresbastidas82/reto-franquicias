# Franchise Service

Reactive API for managing franchises, branches and products, built with Spring Boot 3 and WebFlux.

## Prerequisites

- Java 17+
- Gradle 8+
- PostgreSQL 14+

## Database setup

Create the database in PostgreSQL:

```sql
CREATE DATABASE franchise_db;
```

The application uses the following environment variables (with default values for local development):

| Variable      | Default value               |
|---------------|-----------------------------|
| `DB_URL`      | `localhost:5432/franchise_db` |
| `DB_USERNAME` | `postgres`                  |
| `DB_PASSWORD` | `123456`                    |

## Running locally

```bash
# Clone the repository
git clone https://github.com/andresbastidas82/reto-franquicias.git

# Build the project
./gradlew build

# Run the application
./gradlew bootRun
```

The application starts at `http://localhost:8081`.

## Running tests

```bash
./gradlew test
```

The coverage report (JaCoCo) is generated at `build/reports/jacoco/test/html/index.html`.

## API Documentation (Swagger)

With the application running, go to:

- Swagger UI: `http://localhost:8081/webjars/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8081/v3/api-docs`

## Main endpoints

| Method | Path                                      | Description                              |
|--------|-------------------------------------------|------------------------------------------|
| POST   | `/api/v1/franchise`                       | Create franchise                         |
| PATCH  | `/api/v1/franchise/{id}`                  | Update franchise name                    |
| POST   | `/api/v1/branch`                          | Create branch                            |
| PATCH  | `/api/v1/branch/{id}`                     | Update branch name                       |
| POST   | `/api/v1/products`                        | Create product                           |
| DELETE | `/api/v1/products/{id}`                   | Delete product                           |
| PATCH  | `/api/v1/products/{id}/stock`             | Update product stock                     |
| PATCH  | `/api/v1/products/{id}/name`              | Update product name                      |
| GET    | `/api/v1/products/{franchiseId}/top-stock`| Top stock product per branch             |

## Project structure

The project follows a **hexagonal architecture** (ports and adapters):

```
src/main/java/com/pragma/franchise/
├── application/
│   └── config/                  # Bean configuration (use cases, resilience)
├── domain/
│   ├── api/                     # Input ports (service ports)
│   │   ├── branch/
│   │   ├── franchise/
│   │   └── product/
│   ├── enums/                   # Technical messages and response codes
│   ├── exceptions/              # Domain exceptions
│   ├── model/                   # Domain models (Branch, Franchise, Product)
│   ├── spi/                     # Output ports (persistence ports)
│   └── usecase/                 # Use cases (business logic)
│       ├── branch/
│       ├── franchise/
│       └── product/
├── infrastructure/
│   ├── adapters/
│   │   └── persistenceadapter/  # Persistence adapters (R2DBC)
│   │       ├── entity/          # Database entities
│   │       ├── mapper/          # Entity <-> domain mappers
│   │       ├── repository/      # Reactive repositories
│   │       └── resilience/      # ResilienceHelper (circuit breaker, bulkhead, timeout)
│   └── entrypoints/             # Input adapters (REST API)
│       ├── branch/              # Router, Handler, DTOs, Mapper
│       ├── franchise/
│       ├── product/
│       ├── config/              # OpenAPI/Swagger configuration
│       ├── dto/                 # Shared DTOs (GenericResponse)
│       ├── exception/           # GlobalExceptionHandler
│       └── utils/               # Validator, request param extractor
└── FranchiseApplication.java    # Main class
```

## Resilience patterns

The persistence layer implements three levels of protection using Resilience4j:

1. **Timeout** (2s): Cuts off calls that exceed the time limit.
2. **Bulkhead** (10 concurrent calls): Limits concurrency towards the database.
3. **Circuit Breaker** (50% threshold): Opens the circuit if the failure rate exceeds 50% within a 5-call sliding window.

## Tech stack

- Spring Boot 3.2.5
- Spring WebFlux (reactive)
- R2DBC + PostgreSQL
- Resilience4j (circuit breaker, bulkhead)
- MapStruct
- Lombok
- SpringDoc OpenAPI (Swagger)
- JUnit 5 + Mockito + Reactor Test
- JaCoCo (coverage)
