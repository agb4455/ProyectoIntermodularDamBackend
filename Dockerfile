# Stage 1: Build
FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace/app

# Copy maven wrapper and pom.xml
COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .

# Download dependencies (caching layer)
RUN ./mvnw dependency:go-offline -B

# Copy source and build the application
COPY src src
RUN ./mvnw package -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# Add a non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the built artifact from the build stage
COPY --from=build /workspace/app/target/*.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Health check to ensure the application is running
HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget -qO- http://localhost:8080/actuator/health | grep UP || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]