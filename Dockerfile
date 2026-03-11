# Используем multi-stage build для оптимизации размера образа

# Stage 1: Build
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Копируем Maven wrapper и pom.xml для кэширования зависимостей
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Скачиваем зависимости (этот слой будет кэшироваться)
RUN ./mvnw dependency:go-offline

# Копируем исходный код
COPY src ./src

# Собираем приложение
RUN ./mvnw clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Создаем непривилегированного пользователя
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Копируем JAR из build stage
COPY --from=build /app/target/*.jar app.jar

# Открываем порт
EXPOSE 8081

# Healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8081/actuator/health || exit 1

# Запускаем приложение
ENTRYPOINT ["java", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", \
  "app.jar"]