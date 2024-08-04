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

	implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")

	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}