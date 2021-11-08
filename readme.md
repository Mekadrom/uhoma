Here's how to set up HomeAssistant for yourself using kubernetes (read all steps before deciding what to do):

1. Build the application and export artifacts by running `./gradlew distribute -PbuildProd` in the root of the repository.
    1. Currently, this creates a `dist/` folder with a `server/` artifact and a `frontend/` artifact. The server artifact is a spring bootJar called `server.jar` and the frontend artifact is the output of `ng build --configuration production` if the `-PbuildProd` switch is added to the command line.
2. Build a docker image for each of the following project:
    1. app-server
        1. Run either of the following commands (depending on if you prefer to use img or docker) in the root of the repository:
            1. `docker build . -f env/kubernetes/server/Dockerfile -t <build_tag> --build-arg artifactPath='dist/server/'`
            2. `img build . -f env/kubernetes/server/Dockerfile -t <build_tag> --build-arg artifactPath='dist/server/'`
    2. frontend
        1. Run either of the following commands (depending on if you prefer to use img or docker) in the root of the repository:
            1. `docker build . -f env/kubernetes/server/Dockerfile -t <build_tag> --build-arg artifactPath='dist/frontend/'`
            2. `img build . -f env/kubernetes/frontend/Dockerfile -t <build_tag> --build-arg artifactPath='dist/frontend/'`
3. If you prefer to use a remote artifact registry (like GCR or Google Artifact Registry), tag the images in the commands from the previous step according to your repo's location.
    1. Eg. for my GCP Artifact Registry, I would tag each image with `us-east1-docker.pkg.dev/root-furnace-306909/hadocker-images/ha-<app_name>:1.0.0-main` in order to push up.
    2. You'll also have to set up authentication with this repository to push. For Google, you can run `gcloud auth login`, login in the browser, and then run `gcloud auth configure-docker` to configure authentication with your project's repositories.
4. Push using either `docker push <image_tag>` or `img push <image_tag>`
5. Once the images have been pushed up, create a kubernetes cluster. For this tutorial I am using minikube, but this can be done in the cloud with some additional configuration (TODO).
    1. Run `minikube start --nodes=3` to create a cluster with the default name and two nodes other than the master node.
    2. Label each node with the "worker" label for pods to be able to be deployed to them:
        1. Run `kubectl label node minikube-m02 node-role.kubernetes.io/worker=worker`
        2. Run `kubectl label node minikube-m03 node-role.kubernetes.io/worker=worker`
    3. If using a remote repository, create an image pull secret that authenticates with this repository. For GCP, this means creating a service account that has access to the repository and exporting a json-key credential file. This can be imported into kubernetes by running this command:
        1. `kubectl create secret docker-registry gcr-json-key --docker-server=us-east1-docker.pkg.dev --docker-username=_json_key --docker-password="$(cat ~/json_key.json)" --docker-email=<email_address>`
            1. Where `gcr-json-key` is the name of the secret to use in deployments, the docker-server is the zone of the repository, docker-password is the contents of the key file, and docker-email is any email address. docker-username has to be `_json_key` for this secret to work.
    4. If using docker images built and hosted locally, simply run `eval $(minikube docker-env)` before starting and pass the `--insecure-registry` flag to the `minikube start` command in step 1.
6. You may need to make the following modifications before deploying to kubernetes:
    1. Modify the image names for `env/kubernetes/server/server-deployment.yml` and `env/kubernetes/frontend/frontend-deployment.yml` to pull the correct images for the app server and frontend
    2. Modify the imagePullSecrets in the server and frontend deployments to whatever you named the secret in the previous steps.
    3. Modify any environment variables (like the JWT signing key for the app server [RECOMMENDED]) to fit your needs (stay away from the IP address ones).
    4. Change the default postgres credentials in `env/kubernetes/postgres/postgres-configmap.yml`
    5. Any additional modifications to make your cluster run smoothly according to your needs, including number of instances for each deployment.
7. Once the cluster has started, run `kubectl kustomize -o deployment.yml` in `env/kubernetes/`. This will take every deployment, service and other config yaml files in the other directories in `env/kubernetes/` and merge them into one file named `deployment.yml`.
8. Run `kubectl apply -f deployment.yml` to run the deployment. This should pull any necessary images and instantiate pods on any number of the nodes in the cluster, and execute their startup commands. When every pod is in the 'Ready' state, as shown by running `kubectl get pods`, you can continue.
9. If running in minikube, run `nohup minikube tunnel &` or just `minikube tunnel` if you want to easily `Ctrl+C` to exit. This makes minikube expose the LoadBalancer services to localhost like a cloud LoadBalancer would grant external IP addresses.
10. The frontend will attempt to connect to `http://app-server:8080`. In a production environment, this will have been deployed with an actual url for the server and it would resolve properly. Locally, you will have to modify your hosts file to make `app-server` route to `127.0.0.1`. The host file on Windows is at `C:\Windows\System32\drivers\etc\hosts`,
11. The last thing that needs to be done is the deltarunning against the database. In `hadb/`, run `./initdb.sh` to create the initial schema to create objects in, and then use that schema when running `./deltarun.sh` to deploy the schema and load any additional test data you might have, as defined in `hadb/delta/data/test_data.sql` (a .gitignored file),

After following these instructions, HomeAssistant should be ready for use. The default password using the JWT signing key of 'devsigningkey' is automatically inserted into the database. You can run an UpdatableBCrypt simulation with another signing key and update the record to change the password in order to log in and use the app (recommended).

Connect to the server at `http://localhost:8080` (unauthenticated, should have a secure proxy in front of it).
Connect to the frontend at `http://localhost:4200` (despite being the developer port for an angular UI, this is running in production mode and mapped to this port because 8080 is already exposed).
Connect to kafdrop at `http://localhost:9000` (unauthenticated, should have a secure proxy in front of it).
Connect to postgres at `http://localhost:5432` (or by using the connection string `postgres://<username>:<password>@localhost:5432/hams_data`) (unauthenticated, should have a secure proxy in front of it).

Tips/Troubleshooting:

To tear down the whole thing to start anew, run `minikube delete`.
To delete one deployment (like the app-server0 or frontend0 deployments for testing changes in kubernetes), run `kubectl get deploy` to list deployments, then run `kubectl delete deploy <deployment_name>`. To reapply using changes in the images, run `kubectl kustomize -o deployment.yml && kubectl apply -f deployment.yml`.

The pods may start out of dependency order. They will be continually restarted by the kube control plane until the dependencies are resolved. Pods do not get restarted until every other pod has finished its 'ContainerCreating' phase. Most commonly, the app-server pod(s) will fail initially because the postgres pod takes too long to create. 
It takes about 12 minutes for every pod to have a 'Ready' state.  

Here is the current planned architecture of the project, in a diagram that showcases how each part might communicate with the others:

![](server/src/main/resources/readme/architecture.png)

Here is an up-to-date view of the standard node angular ui:

Login screen:

![](frontend/src/assets/readme/login.png)


Dashboard:

![](frontend/src/assets/readme/dashboard.png)