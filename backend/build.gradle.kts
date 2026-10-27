// Backend de Ale y Leo: Spring Boot 3 + Kotlin + MySQL (sección 4.3 del plan).
plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.spring") version "2.0.21" // abre las clases para que Spring pueda crear proxies
    kotlin("plugin.jpa") version "2.0.21"    // constructores vacíos para las entidades JPA
    id("org.springframework.boot") version "3.5.16"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.aleyleo"
version = "0.75.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")        // API REST
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")   // JPA / Hibernate
    implementation("org.springframework.boot:spring-boot-starter-validation") // @NotBlank, @Email...
    implementation("org.springframework.boot:spring-boot-starter-security")   // BCrypt y roles
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")      // JSON <-> data class
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    runtimeOnly("com.mysql:mysql-connector-j")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

// Las entidades JPA deben poder heredarse (Hibernate crea proxies de ellas).
allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
