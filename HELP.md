# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/4.0.3/gradle-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/4.0.3/gradle-plugin/packaging-oci-image.html)
* [Spring Data JPA](https://docs.spring.io/spring-boot/4.0.3/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [Liquibase Migration](https://docs.spring.io/spring-boot/4.0.3/how-to/data-initialization.html#howto.data-initialization.migration-tool.liquibase)
* [Spring Web](https://docs.spring.io/spring-boot/4.0.3/reference/web/servlet.html)

### Guides
The following guides illustrate how to use some features concretely:

* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)

### Additional Links
These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

### Profiles

The project now uses Spring profiles with separate config files:

* `application.yml` — shared base config
* `application-dev.yml` — local development profile
* `application-prod.yml` — production profile
* `application-test.yml` — test profile

Default profile:

* `dev`

Run with an explicit profile:

```powershell
./gradlew bootRun --args='--spring.profiles.active=dev'
```

```powershell
./gradlew bootRun --args='--spring.profiles.active=prod'
```

```powershell
./gradlew test --args='--spring.profiles.active=test'
```

Swagger behavior by profile:

* `dev` — Swagger UI enabled
* `prod` — Swagger UI and `/v3/api-docs` disabled
* `test` — Swagger disabled
