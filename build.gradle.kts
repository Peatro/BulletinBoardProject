plugins {
	java
	id("org.springframework.boot") version "4.0.3"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.peatroxd"
version = "0.0.1-SNAPSHOT"
description = "BulletinBoardProject"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// --- Spring ---
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")

	// --- OAuth2 (Keycloak) ---
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

	// --- Liquibase ---
	implementation("org.springframework.boot:spring-boot-starter-liquibase")

	// --- DB ---
	runtimeOnly("org.postgresql:postgresql")

	// --- Mapper ---
	implementation("org.mapstruct:mapstruct:1.5.5.Final")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")

	annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

	// --- Lombok ---
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	// --- OpenAPI ---
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.0")

	// --- Test ---
	testImplementation(platform("org.testcontainers:testcontainers-bom:1.20.4"))
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:postgresql")
	testImplementation("org.mockito:mockito-core")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
