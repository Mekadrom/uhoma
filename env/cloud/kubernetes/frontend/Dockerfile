FROM angular/ngcontainer:latest

ARG artifactPath

EXPOSE 4000 4200 4433 5000 8080 9876

RUN mkdir ~/ng && \
    apt-get update && \
    apt-get install -y unzip

COPY "${artifactPath}" ~/ng-app/artifact.zip

WORKDIR ~/ng-app

RUN unzip artifact.zip

ENTRYPOINT ["ng", "serve", "--configuration"]

CMD ["production"]
