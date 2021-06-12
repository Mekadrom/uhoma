Prerequisites:
* Have psql installed
* Have docker installed

To create and start a postgres container for this database, run:
    `docker run -d -p 5432:5432 -P --name hamsdb-container -e POSTGRES_USER=hams_data -e POSTGRES_PASSWORD=hams_data postgres`

If the container did not start, run the command again.

This will create a postgreSQL database docker container named hamsdb-container.

You can connect to it using psql: `psql postgres://hams_data:hams_data@localhost:5432/hams_data`
