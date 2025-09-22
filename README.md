# Invoice Processor  Service

A Spring Boot application that provides APIs for invoice processing.  
Includes **global exception handling**, **JUnit + MockMvc tests**, and a **Docker setup** using Java 21 Alpine.

---

## 🚀 Features
- REST APIs for invoice operations.
- Global exception handling with `@ControllerAdvice`.
- Unit & integration tests with **JUnit 5** and **MockMvc**.
- Dockerized build with **multi-stage Dockerfile** (Maven + Java 21 Alpine).
- Lightweight runtime image (~100MB).
- Ready for CI/CD pipelines.

---

## 🛠️ Tech Stack
- **Java 21**
- **Spring Boot 3.x**
- **Maven**
- **JUnit 5 + MockMvc**
- **Docker (Alpine)**

---


## 🧪 Running Tests
```bash
mvn clean test
```

---

## 🐳 Docker Setup

### 1. Build Docker Image
```bash
docker build -t invoice-service:latest .
```

### 2. Run Container
```bash
docker run -p 8080:8080 invoice-service:latest
```


## 📦 Docker Compose
### 1. Run it with:
```bash
docker-compose up --build
```


### 3. Access API
- Base URL: `http://localhost:8080`

---

## 📖 API Documentation
If you enabled Swagger/OpenAPI:
- Swagger UI → `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON → `http://localhost:8080/v3/api-docs`