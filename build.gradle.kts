import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.3.1"
    id("io.spring.dependency-management") version "1.1.5"
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.spring") version "1.9.24"
    kotlin("kapt") version "1.9.24"
}

java.sourceCompatibility = JavaVersion.VERSION_19
java.targetCompatibility = JavaVersion.VERSION_19

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    group = "satisfied.be.dont"
    version = "0.0.1-SNAPSHOT"

//    kotlin {
//        compilerOptions {
//            freeCompilerArgs.addAll("-Xjsr305=strict")
//        }
//    }
    tasks.withType<KotlinCompile> {
        compilerOptions {
            freeCompilerArgs.addAll("-Xjsr305=strict")
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}


