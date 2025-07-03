# Start with a base image containing Java runtime
FROM openjdk:17-jdk-slim

# Add a volume pointing to /tmp
VOLUME /tmp

# Set the working directory inside the container
WORKDIR /app

# Copy the jar file to the container
COPY target/*.jar app.jar

# Expose the port (Render injects PORT env variable)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
