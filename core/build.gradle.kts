plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("kapt")
}


tasks.getByName("bootJar") {
    enabled = false // 실행 가능한 jar 파일을 생성한다. https://www.devkuma.com/docs/gradle/bootjar-jar/
}

tasks.getByName("jar") {
    enabled = true // 클래스 파일을 생성한다. https://www.devkuma.com/docs/gradle/bootjar-jar/
}

dependencies {

    //================================
    // Basic
    //================================
    compileOnly("org.springframework.boot:spring-boot-starter")
    compileOnly("org.jetbrains.kotlin:kotlin-reflect")
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    compileOnly("org.springframework.boot:spring-boot-starter-webflux")
    compileOnly("org.springframework.boot:spring-boot-starter-validation")
    compileOnly("org.springframework.boot:spring-boot-starter-security")
    compileOnly("org.springframework.boot:spring-boot-starter-aop")
    // for property
    // https://perfectacle.github.io/2021/11/21/spring-boot-configuration-processor/
    // https://docs.spring.io/spring-boot/docs/current/reference/html/configuration-metadata.html#appendix.configuration-metadata.annotation-processor
    kapt("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    //================================
    // Database
    //================================
    compileOnly("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    compileOnly("org.springframework.boot:spring-boot-starter-data-redis-reactive")


    //================================
    // JsonWebToken
    //================================
    compileOnly("io.jsonwebtoken:jjwt-api:0.12.6")
    compileOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    compileOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")


    //================================
    // Email
    //================================
    compileOnly("org.springframework.boot:spring-boot-starter-mail")


    //================================
    // Utils
    //================================
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("commons-codec:commons-codec:1.17.1")
    implementation("com.google.guava:guava:33.2.1-jre")
    implementation("org.reflections:reflections:0.10.2")


    //================================
    // Swagger
    //================================
    compileOnly("org.springdoc:springdoc-openapi-starter-webflux-ui:2.6.0")
}