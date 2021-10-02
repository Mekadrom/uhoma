1. Copy `docker-compose-template.yml` to `docker-compose.yml`
1. Replace the occurrences of `{{ ip_address }}` in docker-compose.yml with your host's ip address.
    * Run `/sbin/ip route|awk '/default/ { print $3 }'` to find out what to use.
1. Replace the occurrence of `{{ docker_sock }}` with the location of the host's docker sock file.
    * On linux, this is /var/run/docker.sock
1. Replace the occurrence of `{{ kafka_volume }}` with a location for kafka to store its logs (event data)
    * This can simply be /kafka
1. Run `docker-compose up -d` in this directory. This will start a kafka fullstack set of containers. 
