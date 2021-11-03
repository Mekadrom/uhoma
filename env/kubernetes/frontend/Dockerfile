FROM us-east1-docker.pkg.dev/root-furnace-306909/hadocker-images/ha-frontend-base:latest

ARG artifactPath

EXPOSE 4000 4200 4433 5000 8080 9876

RUN apt update -y && \
    apt upgrade -y && \
    mkdir ~/ng-app

COPY "${artifactPath}" ~/ng-app/

WORKDIR ~/ng-app

RUN ng build

ENTRYPOINT ["ng", "serve", "--configuration"]

CMD ["production"]
