FROM debian:bullseye

ARG serviceAccount
ARG accessTokenFilePath

ENV PYTHONUNBUFFERED = 1

USER root

COPY ${accessTokenFilePath} ~/accesstoken

RUN apt update -y && \
    apt upgrade -y && \
    apt install -y openssl npm python3 python3-pip libseccomp-dev curl && \
    curl -fSL "https://github.com/genuinetools/img/releases/download/v0.5.11/img-linux-amd64" -o "/usr/local/bin/img" && \
    chmod a+x "/usr/local/bin/img" && \
    curl -o gcloud.tar.gz -O https://dl.google.com/dl/cloudsdk/channels/rapid/downloads/google-cloud-sdk-361.0.0-linux-x86_64.tar.gz && \
    tar -xvzf gcloud.tar.gz && \
    ln -sf python3 /usr/bin/python && \
    pip3 install --no-cache --upgrade pip setuptools && \
    curl https://sdk.cloud.google.com | bash > /dev/null && \
    export PATH=$PATH:/root/google-cloud-sdk/bin && \
    gcloud auth login ${serviceAccount} --no-launch-browser --access-token-file "~/accesstoken" && \
    gcloud components update --quiet && \
    gcloud components install kubectl --quiet

ENV PATH $PATH:/root/google-cloud-sdk/bin

CMD [ 'cat' ]
