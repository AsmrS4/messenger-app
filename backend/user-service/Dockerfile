FROM gradle:8.14-jdk21 AS builder
WORKDIR /build

COPY gradlew .
COPY gradle ./gradle
COPY settings.gradle .
COPY build.gradle .
COPY buildSrc ./buildSrc

RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

COPY user-service ./user-service
RUN ./gradlew :user-service:bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /build/user-service/build/libs/*.jar app.jar
EXPOSE 8001
ENTRYPOINT ["java", "-jar", "app.jar"]
