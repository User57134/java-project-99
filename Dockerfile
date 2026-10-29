# --- Этап 1 ---
# Берем Linux с установленным JDK
FROM eclipse-temurin:21-jdk-jammy AS build

# Создаем рабочую директорию
WORKDIR /workspace

# Копируем туда файлы проекта с настройками и кодом для компиляции
COPY app/gradlew ./
COPY app/gradle ./gradle
COPY app/settings.gradle.kts app/build.gradle.kts ./

# Скачиваем зависимости (если файл build.gradle.kts не изменился,
# то повторно зависимости не будут скачиваться, а возьмутся из кэша.
RUN ./gradlew dependencies --no-daemon || true

# После этого можно копировать исходный код - который постоянно меняется,
# и все последующие слои уже не смогут браться из кэша.
COPY app/src ./src

# Запуск построения решения, результат сохраняется в папку workspace/build/libs
RUN ./gradlew bootJar -x test

# --- Этап 2 ---
# Берем Linux с установленным JRE
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Возьмем 1 этап и скопируем оттуда из построенный исполняемый файл:
COPY --from=build /workspace/build/libs/*.jar app.jar

# Запуск исполняемого файла
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT:-8080}"]
