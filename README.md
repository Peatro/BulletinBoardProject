# BulletinBoardProject

Backend for a bulletin board application built with Spring Boot, PostgreSQL, Keycloak, Liquibase, and JWT-based authentication.

The application provides:

- user registration and login through Keycloak
- local user profiles synchronized with Keycloak
- advertisement creation, update, publication, and deletion
- public advertisement browsing with filters
- category catalog with seeded data
- admin endpoints for user management
- OpenAPI / Swagger in the `dev` profile

## Tech Stack

| Area | Technology |
| --- | --- |
| Language | Java 21 |
| Framework | Spring Boot 4 |
| Build | Gradle Kotlin DSL |
| Database | PostgreSQL 15 |
| Auth | Keycloak 24 + Spring Security OAuth2 Resource Server |
| Migrations | Liquibase |
| API Docs | springdoc OpenAPI / Swagger UI |
| Mapping | MapStruct |
| Tests | JUnit 5, Mockito, Testcontainers |
| Containerization | Docker, Docker Compose |

## Main Modules

- `auth` - registration and login
- `user` - current profile and admin user management
- `advertisement` - public queries and owner actions for advertisements
- `category` - category catalog
- `security` - JWT authentication, role mapping, and current-user resolution
- `common` - shared exceptions and infrastructure utilities

## Security Model

The backend runs as an OAuth2 resource server and validates JWTs issued by Keycloak.

Imported realm roles:

- `USER`
- `ADMIN`
- `VIEWER`

Public routes:

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /advertisements`
- `GET /advertisements/{id}`
- `GET /api/categories`
- Swagger endpoints in the `dev` profile

All other routes require a bearer token. Some user endpoints additionally require `ADMIN`.

## Quick Start

### Prerequisites

- Java 21
- Docker
- Docker Compose

### 1. Start infrastructure

```powershell
docker compose up -d
```

This starts:

- PostgreSQL on `localhost:5432`
- Keycloak on `http://localhost:8081`

Keycloak imports realm configuration automatically from [`infra/keycloak/board-realm.json`](infra/keycloak/board-realm.json).

### 2. Run the backend

```powershell
./gradlew.bat bootRun
```

The application starts on `http://localhost:9090` using the `dev` profile by default.

### 3. Open API documentation

Swagger UI:

```text
http://localhost:9090/swagger-ui/index.html
```

OpenAPI spec:

```text
http://localhost:9090/v3/api-docs
```

## Configuration

Base application settings are defined in [`src/main/resources/application.yml`](src/main/resources/application.yml).

Profiles:

- `dev` - local development, Swagger enabled, SQL logging enabled
- `prod` - externalized datasource config, Swagger disabled
- `test` - test datasource config, Swagger disabled

### Important Environment Variables

| Variable | Default | Description |
| --- | --- | --- |
| `SERVER_PORT` | `9090` | Application port |
| `DB_URL` | `jdbc:postgresql://localhost:5432/board` in `dev` | PostgreSQL JDBC URL |
| `DB_USERNAME` | `postgres` in `dev` | Database username |
| `DB_PASSWORD` | `postgres` in `dev` | Database password |
| `KEYCLOAK_BASE_URL` | `http://localhost:8081` | Keycloak base URL |
| `KEYCLOAK_REALM` | `board-realm` | Realm used by the backend |
| `KEYCLOAK_TOKEN_REALM` | same as realm | Realm used for admin token retrieval |
| `KEYCLOAK_CLIENT_ID` | `board-backend` | Keycloak client id |
| `KEYCLOAK_CLIENT_SECRET` | empty by default in config | Keycloak client secret |
| `KEYCLOAK_ADMIN` | `admin` | Keycloak admin username |
| `KEYCLOAK_ADMIN_PASSWORD` | `admin` | Keycloak admin password |
| `JWT_ISSUER_URI` | derived from Keycloak base URL + realm | JWT issuer URI for resource server |

There is also a local [`.env`](.env) file in the repository with development values that match the imported Keycloak realm and local PostgreSQL setup.

## Default Local Credentials

Keycloak admin console:

- URL: `http://localhost:8081`
- username: `admin`
- password: `admin`

Imported Keycloak client:

- client id: `board-backend`
- client secret: `Qd5eK8Yy4YgwHtkRaZ4sOtra4m7eK6JI`

## Database and Migrations

Liquibase is enabled by default. The master changelog is located at [`src/main/resources/db/changelog/db.changelog-master.yaml`](src/main/resources/db/changelog/db.changelog-master.yaml).

Currently the changelog includes:

- base schema creation
- category seed data
- later incremental changes under `db/changelog/2026/...`

The category seed contains a ready-made hierarchy including groups such as:

- Transport
- Real estate
- Electronics
- Clothing and personal items
- Home and garden
- Jobs
- Services
- Hobbies and leisure

## API Overview

### Authentication

| Method | Path | Description |
| --- | --- | --- |
| `POST` | `/api/auth/register` | Create a user in Keycloak and local DB |
| `POST` | `/api/auth/login` | Get access and refresh tokens |

Register request example:

```json
{
  "username": "alice",
  "email": "alice@example.com",
  "firstName": "Alice",
  "lastName": "Smith",
  "phone": "+70000000000",
  "password": "secret123"
}
```

Login request example:

```json
{
  "username": "alice",
  "password": "secret123"
}
```

### Categories

| Method | Path | Description |
| --- | --- | --- |
| `GET` | `/api/categories` | Get all categories |

### Advertisements

| Method | Path | Auth | Description |
| --- | --- | --- | --- |
| `GET` | `/advertisements` | No | List advertisements with optional filters |
| `GET` | `/advertisements/{id}` | No | Get advertisement by id |
| `POST` | `/advertisements` | Yes | Create advertisement |
| `GET` | `/advertisements/me` | Yes | Get current user advertisements |
| `PUT` | `/advertisements/{id}` | Yes | Update own advertisement |
| `DELETE` | `/advertisements/{id}` | Yes | Delete own advertisement |
| `PATCH` | `/advertisements/{id}` | Yes | Publish own advertisement |

Supported filters for `GET /advertisements`:

- `categoryId`
- `status`
- `authorId`

Create or update advertisement example:

```json
{
  "title": "Toyota Camry 2018",
  "description": "Single owner, good condition, serviced regularly.",
  "price": 1250000.00,
  "categoryId": 101,
  "advertisementType": "SELL"
}
```

### Users

| Method | Path | Auth | Description |
| --- | --- | --- | --- |
| `GET` | `/api/users/me` | Yes | Get current user profile |
| `PUT` | `/api/users/me` | Yes | Update current user profile |
| `DELETE` | `/api/users/me` | Yes | Delete current user profile |
| `GET` | `/api/users/{id}` | Admin | Get user by id |
| `PUT` | `/api/users/{id}` | Admin | Update user as admin |
| `DELETE` | `/api/users/{id}` | Admin | Delete user as admin |

Current user update example:

```json
{
  "email": "alice@example.com",
  "firstName": "Alice",
  "lastName": "Smith",
  "phone": "+70000000000"
}
```

Admin user update example:

```json
{
  "username": "alice",
  "email": "alice@example.com",
  "firstName": "Alice",
  "lastName": "Smith",
  "phone": "+70000000000",
  "role": "ADMIN",
  "enabled": true
}
```

## Running Tests

Run the full test suite:

```powershell
./gradlew.bat test
```

Run a single test class:

```powershell
./gradlew.bat test --tests "com.peatroxd.bulletinboardproject.user.service.UserServiceImplTest"
```

## Build and Run with Docker

Build the application image:

```powershell
docker build -t bulletin-board-app .
```

Run the image against local PostgreSQL and Keycloak:

```powershell
docker run --rm -p 9090:9090 ^
  -e SPRING_PROFILES_ACTIVE=prod ^
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/board ^
  -e DB_USERNAME=postgres ^
  -e DB_PASSWORD=postgres ^
  -e KEYCLOAK_BASE_URL=http://host.docker.internal:8081 ^
  -e KEYCLOAK_REALM=board-realm ^
  -e KEYCLOAK_CLIENT_ID=board-backend ^
  -e KEYCLOAK_CLIENT_SECRET=Qd5eK8Yy4YgwHtkRaZ4sOtra4m7eK6JI ^
  -e KEYCLOAK_ADMIN=admin ^
  -e KEYCLOAK_ADMIN_PASSWORD=admin ^
  bulletin-board-app
```

## Useful Commands

Start infrastructure:

```powershell
docker compose up -d
```

Stop infrastructure:

```powershell
docker compose down
```

Run the backend:

```powershell
./gradlew.bat bootRun
```

Create a runnable jar:

```powershell
./gradlew.bat bootJar
```

## Project Structure

```text
.
|-- infra/
|   `-- keycloak/               # Imported Keycloak realm
|-- src/
|   |-- main/
|   |   |-- java/com/peatroxd/bulletinboardproject/
|   |   |   |-- advertisement/
|   |   |   |-- auth/
|   |   |   |-- category/
|   |   |   |-- common/
|   |   |   |-- image/
|   |   |   |-- security/
|   |   |   `-- user/
|   |   `-- resources/
|   |       |-- application*.yml
|   |       `-- db/changelog/
|   `-- test/
|       `-- java/
|-- docker-compose.yml
|-- Dockerfile
`-- build.gradle.kts
```

## Notes

- Swagger is available only in the `dev` profile.
- Registration is handled by the backend, not by self-service Keycloak registration.
- User changes are synchronized with Keycloak from the service layer.
- Liquibase should remain the source of truth for schema changes; add new migrations instead of rewriting applied ones.
