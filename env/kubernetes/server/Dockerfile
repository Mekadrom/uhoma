FROM openjdk:17-jdk-bullseye

ARG artifactPath

ARG javaArgs="-Dspring.profiles.active=prod"

EXPOSE 8080

RUN apt upgrade -y && \
    apt update -y && \
    mkdir ~/server

COPY "${artifactPath}" ~/server

WORKDIR ~/server

ENTRYPOINT ["java", "-jar", "server.jar"]

CMD ["${javaArgs}"]