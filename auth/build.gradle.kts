plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
}

tasks.getByName("bootJar") {
    enabled = false // 실행 가능한 jar 파일을 생성한다. https://www.devkuma.com/docs/gradle/bootjar-jar/
}

tasks.getByName("jar") {
    enabled = true // 클래스 파일을 생성한다. https://www.devkuma.com/docs/gradle/bootjar-jar/
}

dependencies {
    implementation(project(":core"))


    //================================
    // Database
    //================================
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")


    //================================
    // Ktor http client
    //================================
    implementation("io.ktor:ktor-client-core:2.3.12")
    implementation("io.ktor:ktor-client-cio:2.3.12")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.12")
    implementation("io.ktor:ktor-serialization-jackson:2.3.12")


    //================================
    // JsonWebToken
    //================================
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")


    //================================
    // Email
    //================================
    implementation("org.springframework.boot:spring-boot-starter-mail")


    //================================
    // Swagger
    //================================
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.6.0")


    //================================
    // Basic
    //================================
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("io.ktor:ktor-client-cio-jvm:2.3.12")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
//    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    //================================
    // Validation
    //================================
    implementation("org.passay:passay:1.6.4")
}