To build image run:
    `sudo docker build -t kafka-fullstack .`

To run image:
    `sudo docker run -d --name kafka-fullstack-container -p 9092:9092 -p 9000:9000 -p 2181:2181 -v "$(pwd)"/volume:/kafka kafka-fullstack`
