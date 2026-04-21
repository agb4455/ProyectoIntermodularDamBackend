FROM eclipse-temurin:25-jre

RUN addgroup --system appgroup && adduser --system appuser --ingroup appgroup

USER appuser
WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]