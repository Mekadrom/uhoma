FROM openjdk:17

ARG artifactPath

ARG javaArgs="-Dspring.profiles.active=prod"

EXPOSE 8080

RUN mkdir ~/ng && \
    apt-get update && \
    apt-get install -y unzip

COPY "${artifactPath}" ~/appserver/appserver.zip

RUN unzip artifact.zip

WORKDIR ~/appserver

ENTRYPOINT ["java", "-jar", "server.jar"]

CMD ["${javaArgs}"]