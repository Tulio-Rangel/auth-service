FROM openjdk:17-jdk-slim-buster

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY src src

# Establece permisos de ejecución para gradlew y construye la aplicación
RUN chmod +x ./gradlew && ./gradlew build -x test

# Puerto que expone la aplicación
EXPOSE 8082

# Obtener la versión desde Gradle
ARG APP_VERSION
ENV APP_VERSION=${APP_VERSION}

# Comando para ejecutar la aplicación
ENTRYPOINT ["java","-jar","build/libs/AuthService-0.0.1-SNAPSHOT.jar"]