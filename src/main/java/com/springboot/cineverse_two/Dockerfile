FROM openjdk:21

WORKDIR /app
COPY target/cineverse_two-0.0.1-SNAPSHOT.jar /app/cineverse_two-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT["java", "-jar", "cineverse_two-0.0.1-SNAPSHOT.jar"]