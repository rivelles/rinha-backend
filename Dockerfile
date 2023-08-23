FROM openjdk:17-alpine
EXPOSE 8080
RUN mkdir -p /app/
ADD build/libs/rinha-backend-0.0.3-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
