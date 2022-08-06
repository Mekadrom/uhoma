FROM openjdk:17-alpine

ARG artifactPath

ARG javaArgs="-Dspring.profiles.active=prod"

ENV SERVER_JAVA_ARGS=${javaArgs}

EXPOSE 8081

RUN mkdir ~/actionserver

COPY "${artifactPath}" ~/actionserver

WORKDIR ~/actionserver

ENTRYPOINT java $SERVER_JAVA_ARGS -jar actionserver.jar
