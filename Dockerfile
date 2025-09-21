FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Copy pom.xml and download dependencies (cache layer)
COPY pom.xml .


# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests

# -----------------------------
# 2. Runtime stage
# -----------------------------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy only the fat jar from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose app port
EXPOSE 8080

# Run Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
