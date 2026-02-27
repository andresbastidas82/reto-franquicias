# Franchise Service

Reactive API for managing franchises, branches and products, built with Spring Boot 3 and WebFlux.

## Prerequisites

- Java 17+
- Gradle 8+
- PostgreSQL 14+
- Docker & Docker Compose (optional, for containerized execution)

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

## Running with Docker

```bash
# Build and start all services (app + PostgreSQL)
docker-compose up --build

# Run in background
docker-compose up --build -d

# Stop services
docker-compose down

# Stop and remove volumes (clears database)
docker-compose down -v
```

The application will be available at `http://localhost:8081`.

## Running locally (without Docker)

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

## Deployment to AWS

The `terraform/` directory contains the IaC to deploy the full solution on AWS:

- VPC with public/private subnets, NAT Gateway
- API Gateway HTTP (public entry point, free tier: 1M requests/month)
- ALB internal (routes traffic from API Gateway to ECS)
- ECS Fargate (0.25 vCPU, 512MB - minimal config)
- RDS PostgreSQL db.t3.micro (free tier: 750h/month, 20GB gp2)
- ECR (container registry)
- CloudWatch Logs (7 days retention)

### Steps

```bash
# 1. Configure variables
cp terraform/terraform.tfvars.example terraform/terraform.tfvars
# Edit terraform.tfvars with your values (especially db_password)

# 2. Initialize and apply Terraform
cd terraform
terraform init
terraform plan
terraform apply

# 3. Push Docker image to ECR
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin <ecr_repository_url>
docker build -t franchise-app .
docker tag franchise-app:latest <ecr_repository_url>:latest
docker push <ecr_repository_url>:latest

# 4. Access the application via API Gateway
# terraform output api_gateway_url
```

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
