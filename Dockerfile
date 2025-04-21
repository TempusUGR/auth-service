FROM amazoncorretto:21-alpine-jdk
WORKDIR /app
EXPOSE 9000
COPY ./target/auth-service-0.0.1-SNAPSHOT.jar auth-service.jar
COPY .env .env

ENTRYPOINT ["java", "-jar", "auth-service.jar"]