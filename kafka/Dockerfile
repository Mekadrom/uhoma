FROM ubuntu

# install openJDK-8 and maven and wait for process to finish (no &)
RUN apt-get update && \
    apt-get install -y openjdk-11-jdk maven && \
    apt-get clean

# set JAVA_HOME and export to working context
ENV JAVA_HOME /usr/lib/jvm/java-11-openjdk-amd64/
RUN export JAVA_HOME
RUN export PATH="$PATH:/usr/lib/jvm/java-11-openjdk-amd64/bin"

RUN mkdir /kafka

WORKDIR /kafka

CMD chmod +x /kafka/wrapper && /kafka/wrapper
