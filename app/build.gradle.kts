plugins {
	java

	alias(libs.plugins.lombok)
	alias(libs.plugins.spring.boot)
	alias(libs.plugins.dependency.management)
}

group = "hexlet.code"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// Подключение базы данных H2
	implementation(libs.h2database)

	// Подключение драйвера Postgresql
	implementation(libs.postgresql)

	// Поддержка jakarta.persistence.*
	implementation(libs.spring.boot.starter.data.jpa)

	// Подключение компонентов для работы с web, библиотеку для работы с JSON
	implementation(libs.spring.boot.starter.webmvc)

	// Подключение компонентов для обычных тестов и тестов web слоя
	testImplementation(libs.spring.boot.starter.webmvc.test)

	// Нужен для обнаружения и запуска тестов JUnit 5 сборщиком Gradle
	testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.withType<Test> {
	useJUnitPlatform()
}
