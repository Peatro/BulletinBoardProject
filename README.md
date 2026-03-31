# BulletinBoardProject

Spring Boot backend for a bulletin board application with PostgreSQL, Keycloak, Liquibase and JWT authentication.

## Requirements

- Java 21
- Docker and Docker Compose

## Local Run

### 1. Start infrastructure

```powershell
docker compose up -d
```

This starts:

- PostgreSQL on `localhost:5432`
- Keycloak on `http://localhost:8081`

Keycloak imports realm config automatically from [infra/keycloak/board-realm.json](/D:/JAVA/Projects/BulletinBoardProject/infra/keycloak/board-realm.json).

### 2. Run the backend

```powershell
./gradlew.bat bootRun
```

The backend starts on `http://localhost:9090` with the default `dev` profile.

### 3. Open Swagger

```text
http://localhost:9090/swagger-ui/index.html
```

Swagger is enabled only in `dev`. In `prod` and `test` profiles both Swagger UI and `/v3/api-docs` are disabled.

## Docker Image

Build the application image:

```powershell
docker build -t bulletin-board-app .
```

Run it against the local infrastructure:

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

Run tests:

```powershell
./gradlew.bat test
```

Stop infrastructure:

```powershell
docker compose down
```
