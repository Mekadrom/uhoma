Here's how to set up HomeAssistant for yourself using kubernetes (read all steps before deciding what to do):

### Prerequisites
1. docker or img
2. minikube (or equivalent) 
3. kubectl (or equivalent)
4. java 17 if running java subprojects outside of containers
5. shell environment (currently only supports bash/linux)

### Setup
#### If working locally only
1. Make a copy of env.gradle.example and remove the .example extension.
2. Configure properties in env.gradle.
3. Create a `json_key.json` file at the root of the repo with dummy contents for local development, e.g.:
   ```json
   {}
   ```
4. Run `./gradlew fullClusterSetupLocal`.
5. Run `kubectl port-forward service/postgresdb 5432:5432` to be able to connect to the postgres database from your host machine.
6. Once every pod has a state of "Running", run `./gradlew initDb`. This will create the schema for the main server.
7. After `initDb` is done, run `./gradlew deltaRun` to initialize the schema and load seed data.

After following these instructions, HomeAssistant should be ready for use. The default password using the JWT signing key of 'devsigningkey' is automatically inserted into the database. You can run an UpdatableBCrypt simulation with another signing key and update the record to change the password in order to log in and use the app (recommended).

Tips/Troubleshooting:

* To tear down the whole thing to start anew, run `minikube stop && minikube delete --purge`.
* To delete one deployment (like the app-server0 or frontend0 deployments for testing changes in kubernetes), run `kubectl get deploy` to list deployments, then run `kubectl delete deploy <deployment_name>`. To reapply using changes in the images, run `./gradlew reloadCluster`.

* The pods may start out of dependency order. They will be continually restarted by the kube control plane until the dependencies are resolved. Pods do not get restarted until every other pod has finished its 'ContainerCreating' phase. Most commonly, the app-server pod(s) will fail initially because the postgres pod takes longer to create, and the kafka-broker pod will fail initially as well because zookeeper takes longer. 
* It takes about 12 minutes for every pod to have a 'Ready' state. This is after pulling new images for both kafka pods and the postgres pods. This can be shortened by manually storing docker images locally and pushing them to the minikube cluster once per cluster lifecycle by running `minikube image load <image_name>`.

Here is the current planned architecture of the project, in a diagram that showcases how each part might communicate with the others:

![](server/src/main/resources/readme/architecture.png)

Here is an up-to-date view of the standard node angular ui:

Login screen:

![](frontend/src/assets/readme/login.png)


Dashboard:

![](frontend/src/assets/readme/dashboard.png)
