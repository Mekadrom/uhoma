Here's how to set up HomeAssistant for yourself using kubernetes (read all steps before deciding what to do):

### Prerequisites
1. docker or img
2. minikube (or equivalent) 
3. kubectl (or equivalent)
4. java 17 if running java subprojects outside of containers
5. shell environment (currently only supports bash/linux)

### Setup
#### If using GCR (Google Container Registry) or GAR (Google Artifact Registry)
1. Make a copy of env.gradle.example and remove the .example extension.
2. Configure properties in env.gradle.
3. Download JSON key file for a service account that has access to the registry, place it at the root of this repo. If working with local images, skip this step.
4. Run `./gradlew fullClusterSetup`.
5. Run ./mtunnel.sh to be able to connect to the minikube cluster.
6. Once every pod has a state of "Running", run `./gradlew initDb`. This will create the schema for the main server.
7. After `initDb` is done, run `./gradlew deltaRun` to initialize the schema and load seed data.

#### If working locally only
1. Make a copy of env.gradle.example and remove the .example extension.
2. Configure properties in env.gradle.
3. Create a `json_key.json` file at the root of the repo with dummy contents for local development, e.g.:

   ```json
   {
     "dummy": true
   }
4. Run `./gradlew fullClusterSetupLocal`.
5. Run `minikube service postgresdb --url` to get the URL to connect to for the postgres database. Deconstruct this to put the IP address in as the value for `gradle.ext.dbUrl` and `gradle.ext.dbPort` in env.gradle
6. Once every pod has a state of "Running", run `./gradlew initDb`. This will create the schema for the main server.
7. After `initDb` is done, run `./gradlew deltaRun` to initialize the schema and load seed data.

After following these instructions, HomeAssistant should be ready for use. The default password using the JWT signing key of 'devsigningkey' is automatically inserted into the database. You can run an UpdatableBCrypt simulation with another signing key and update the record to change the password in order to log in and use the app (recommended).

* Connect to the server at `http://localhost:8080` (unauthenticated, should have a secure proxy in front of it).
* Connect to the frontend at `http://localhost:4200` (despite being the developer port for an angular UI, this is running in production mode and mapped to this port because 8080 is already exposed).
* Connect to kafdrop at `http://localhost:9000` (unauthenticated, should have a secure proxy in front of it).
* Connect to postgres at `http://localhost:5432` (or by using the connection string `postgres://<username>:<password>@<minikube ip>:<postgres minikube port>/hams_data`) (unauthenticated, should have a secure proxy in front of it).

Tips/Troubleshooting:

* To tear down the whole thing to start anew, run `minikube stop && minikube delete`.
* To delete one deployment (like the app-server0 or frontend0 deployments for testing changes in kubernetes), run `kubectl get deploy` to list deployments, then run `kubectl delete deploy <deployment_name>`. To reapply using changes in the images, run `./gradlew reloadCluster`.

* The pods may start out of dependency order. They will be continually restarted by the kube control plane until the dependencies are resolved. Pods do not get restarted until every other pod has finished its 'ContainerCreating' phase. Most commonly, the app-server pod(s) will fail initially because the postgres pod takes longer to create, and the kafka-broker pod will fail initially as well because zookeeper takes longer. 
* It takes about 12 minutes for every pod to have a 'Ready' state. This is after pulling new images for all three kafka pods and the postgres pods. This can be shortened by manually storing docker images locally and pushing them to the minikube cluster once per cluster lifecycle by running `minikube image load <image_name>`.

Here is the current planned architecture of the project, in a diagram that showcases how each part might communicate with the others:

![](server/src/main/resources/readme/architecture.png)

Here is an up-to-date view of the standard node angular ui:

Login screen:

![](frontend/src/assets/readme/login.png)


Dashboard:

![](frontend/src/assets/readme/dashboard.png)