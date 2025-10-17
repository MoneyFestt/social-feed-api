FROM openjdk:17-jdk-slim

# Вказуємо робочу директорію
WORKDIR /app


COPY target/social-feed-api-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
