FROM openjdk:21-jdk-slim

WORKDIR /app

RUN apt-get update && \
    apt-get install -y maven curl && \
    rm -rf /var/lib/apt/lists/*

COPY . .

RUN mvn clean package -DskipTests

RUN ls -la target/

RUN cp target/*.jar app.jar

RUN ls -la app.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

CMD ["java", "-jar", "app.jar"]