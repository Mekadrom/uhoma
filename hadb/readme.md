Prerequisites:
* Have psql installed
* Have docker installed

To create and start a postgres container for this database, run:
    `docker run -d -p 5432:5432 -P --name hamsdb-container -e POSTGRES_USER=hams_data -e POSTGRES_PASSWORD=hams_data postgres`

If the container did not start, run the command again.

This will create and run a postgreSQL database docker container named hamsdb-container, with a default database named hams_data and a default user named hams_data with password hams_data. You can change any of these at your discretion.

You can connect to it using psql: `psql postgres://hams_data:hams_data@localhost:5432/hams_data`

If you intend on using the database for the fullstack, and this is your first time running the databse, you will have to manually create the first schema:
1. Run ./initdb.sh -u hams_data -p hams_data -U localhost -P 5432 -d hams_data -s hams_data

To update the database to the correct schema layout, you must also run deltarun.sh:
1. Run ./deltarun.sh -u hams_data -p hams_data -U localhost -P 5432 -d hams_data
