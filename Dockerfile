FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/SE2-Projekt-Server-1.0-SNAPSHOT.jar /app/SE2-Projekt-Server-1.0-SNAPSHOT.jar

CMD["java", "-jar", "/app/SE2-Projekt-Server-1.0-SNAPSHOT.jar"]