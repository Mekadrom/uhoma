FROM openjdk:17-alpine

ARG artifactPath

ARG javaArgs="-Dspring.profiles.active=prod"

ENV SERVER_JAVA_ARGS=${javaArgs}

EXPOSE 8080

RUN mkdir ~/server

COPY "${artifactPath}" ~/server

WORKDIR ~/server

ENTRYPOINT java $SERVER_JAVA_ARGS -jar server.jar
