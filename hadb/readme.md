Prerequisites:
* Have psql installed
* Have docker installed

To create and start a postgres container for this database, run:
    `docker run -d -p 5432:5432 -P --name hamsdb-container -e POSTGRES_USER=hams_data -e POSTGRES_PASSWORD=hams_data postgres`

If the container did not start, run the command again.

This will create a postgreSQL database docker container named hamsdb-container, with a default database named hams_data and a default user named hams_data with password hams_data. You can change any of these at your discretion.

You can connect to it using psql: `psql postgres://hams_data:hams_data@localhost:5432/hams_data`

If you are starting the database for the first time, and you intend on running the fullstack or the main server, you must also run deltarun.sh. Simply running `./deltarun.sh` will print out the usage help message. This will set up the schema with the appropriate tables and seed data.
