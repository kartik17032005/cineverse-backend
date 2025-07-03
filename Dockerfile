# Use Maven to build the project
FROM maven:3.8.1-openjdk-17-slim AS build

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .

RUN mvn dependency:go-offline

# Copy the rest of the project
COPY src ./src

# Package the application
RUN mvn clean package -DskipTests

# Create the final image
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the built jar from the previous build stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
