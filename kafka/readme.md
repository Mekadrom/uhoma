Replace the KAFKA_ADVERTISED_HOST_NAME and KAFKA_BROKERCONNECT options in docker-compose.yml with your host's ip address. 
* Run `/sbin/ip route|awk '/default/ { print $3 }'` to find out what to use.

Then run `docker-compose up -d` in this directory. This will start a kafka fullstack set of containers. 
