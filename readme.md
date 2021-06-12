To run the fullstack Home Assistant, start with kafka. See the readme file in the kafka directory to do this.

To start the database, view the readme under `hadb`.

After the hamsdb and kafka are up and running, you can build and start the main server by running `./gradlew :server:bootRun` in the root of this repo. You can also distribute by running `./gradlew jarAll` and then cding into `dist` and running `./run_server.sh`.

You can start the node server by running `./gradlew :node_backend:bootRun` in the root of this repo. You can also distribute by running `./gradlew jarAll` and then cding into `dist` and running `./run_node.sh`.

You can then start a node ui instance by running `ng serve --open` in the `node-ui` project's directory. This will open the node's ui in your browser.

Here is the current planned architecture of the project, in a diagram that showcases how each part might communicate with the others:

![](server/src/main/resources/readme/architecture.png)
