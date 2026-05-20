# ==========================================
# MULTI-STAGE DOCKERFILE FOR SPRING BOOT
# ==========================================

# Stage 1: Build the application jar cleanly
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the Maven project files
COPY pom.xml .
COPY src ./src

# Run Maven packaging (skip unit tests for faster cloud builds)
RUN mvn clean package -DskipTests

# Stage 2: Create the super lightweight JRE running container
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the built jar from Stage 1
COPY --from=build /app/target/*.jar app.jar

# Define default port & launch command
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
