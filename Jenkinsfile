pipeline {
    agent {
        node {
            label 'build-docker-java17-agent'
            customWorkspace 'workspace/home_assistant/'
        }
    }

    stages {
        stage ('Build artifacts') {
            steps {
                dir ("${WORKSPACE}") {
                    sh (
                        script: './gradlew distribute'
                    )
                }
            }
        }
        stage ('Build docker images') {
            steps {
                dir ("${WORKSPACE}/env/cloud/kubernetes") {
                    dir ("appserver") {
                        sh (
                            script: 'docker build . --build-arg jarfile="${WORKSPACE}/dist/server/server.jar"'
                        )
                    }
                    dir ("consumergroup") {
                        sh (
                            script: ''
                        )
                    }
                    dir ("frontend") {
                        sh (
                            script: 'docker build . --build-arg artifact="${WORKSPACE}/dist/frontend/"'
                        )
                    }
                }
            }
        }
    }
}
