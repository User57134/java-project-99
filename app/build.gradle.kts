plugins {

	// Подключает базовые инструменты компиляции, тестирования и сборки Java в JAR
	java

	// Подключение следующих функци:
	// 1. Упаковывает приложение и все библиотеки в один готовый к запуску JAR
    // 2. Автоматически подбирает совместимые версии для Spring-зависимостей
    // 3. Добавляет команду быстрой сборки и запуска проекта (gradle bootRun)
	alias(libs.plugins.spring.boot)

	// Плагин для генерации классов
	alias(libs.plugins.lombok)

	// Линтер и форматтер
	alias(libs.plugins.spotless)
}

group = "hexlet.code"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

// Настройка форматтера на использование форматирования кода от Google
spotless {
	java {
		importOrder()
		removeUnusedImports()
		googleJavaFormat().aosp()
		formatAnnotations()
		leadingTabsToSpaces(4)
		endWithNewline()
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// Управление версиями для Spring Boot на основе списка совместимости Spring Boot
	implementation(platform(libs.spring.boot.dependencies))

	// Поддержка jakarta.persistence.*
	implementation(libs.spring.boot.starter.data.jpa)

	// Подключение валидации данных (аннотации @NotNull, @Email, @Valid и др.).
    // Необходима для автоматической проверки входящих запросов в контроллерах.
	implementation(libs.spring.boot.starter.validation)

	// Аутентификация, авторизация и защита приложения (включая PasswordEncoder для хэширования паролей)
	implementation(libs.spring.boot.starter.security)

	// Подключение компонентов для работы с web, библиотеку для работы с JSON
	implementation(libs.spring.boot.starter.webmvc)

	// Подключение компонентов для обычных тестов и тестов web слоя
	testImplementation(libs.spring.boot.starter.webmvc.test)

	// Нужен для обнаружения и запуска тестов JUnit 5 сборщиком Gradle
	testRuntimeOnly(libs.junit.platform.launcher)

	// Подключение базы данных H2
	implementation(libs.h2database)

	// Подключение драйвера Postgresql
	implementation(libs.postgresql)

	// Поддержка JsonNullable для корректной обработки частично обновляемых полей
	implementation(libs.jackson.databind.nullable)

	// Генерация кода конвертации между DTO и сущностями
	implementation(libs.mapstruct)

	// Процессор аннотаций MapStruct для генерации кода во время компиляции
	annotationProcessor(libs.mapstruct.processor)

	// Процессор аннотаций MapStruct для генерации кода при тестировании
	testAnnotationProcessor(libs.mapstruct.processor)
}

tasks.withType<Test> {
	useJUnitPlatform()
}
