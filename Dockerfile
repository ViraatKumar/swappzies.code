# Stage 1: Build
FROM maven:3.9-eclipse-temurin-24 AS builder

WORKDIR /app

# Cache dependencies layer
COPY pom.xml ./
RUN mvn dependency:go-offline -q

# Build the app
COPY src ./src
RUN mvn clean package -DskipTests -q

# Stage 2: Runtime
FROM eclipse-temurin:24-jre AS runner

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 5001

ENTRYPOINT ["java", "-jar", "app.jar"]
