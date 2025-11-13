# ===================== Build Stage =====================
FROM eclipse-temurin:17-jdk-focal AS build
WORKDIR /app

# Copy Maven configuration first (caching)
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw

# Download dependencies (offline)
RUN ./mvnw -q -N dependency:go-offline

# Copy source and build jar
COPY src ./src
RUN ./mvnw -DskipTests package -P!native -Dskip.native -e -Dspring.profiles.active=prod

# ===================== Runtime Stage =====================
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Install curl for healthcheck
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Create non-root user
RUN useradd -ms /bin/bash appuser
USER appuser

# Copy built jar from build stage
COPY --from=build --chown=appuser /app/target/*.jar app.jar

# Healthcheck (optional)
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Memory settings (tweak as needed)
ENV JAVA_OPTS="-Xms256m -Xmx1g"

# Expose port (Spring Boot default)
EXPOSE 8080

# Use dynamic port for Render deployment
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /app/app.jar --server.port=${PORT:-8080}" ]
