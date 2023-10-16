FROM gradle:latest AS BUILD

WORKDIR /app
COPY . .
RUN gradle build

FROM openjdk:latest

WORKDIR /app
COPY --from=BUILD . .
EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "app/build/libs/backend-0.0.1-SNAPSHOT.jar" ]