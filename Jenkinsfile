pipeline {
    agent {
        node {
            label 'ha-build'
            customWorkspace 'workspace/home_assistant/'
        }
    }
    parameters {
        string(name: 'BUILD_TAG_PREFIX', defaultValue: '', description: 'A string to prefix the docker image tag names with. Optional.')
        string(name: 'BUILD_TAG_SUFFIX', defaultValue: '', description: 'A string to suffix the docker image tag names with. Optional.')
        string(name: 'DOCKER_REPO_LOCATION', defaultValue: 'us-east1-docker.pkg.dev', description: 'The GCP location of the docker artifact registry.')
        string(name: 'DOCKER_REPO', defaultValue: 'root-furnace-306909/hadocker-images/', description: 'The name of the docker repository in the HomeAssistant GCP project to push images to.')
        extendedChoice(defaultValue: 'server,frontend,loginserver,actionserver', description: '', descriptionPropertyValue: 'server,frontend,loginserver,actionserver', multiSelectDelimiter: ',', name: 'PUSH_IMAGES', quoteValue: false, saveJSONParameterToFile: false, type: 'PT_MULTI_SELECT', value: 'server,frontend,loginserver,actionserver', visibleItemCount: 4)
    }
    environment {
        BUILD_PROPS = readProperties file: "${WORKSPACE}/gradle.properties"
        GROUP = "${env.BUILD_PROPS.group}"
        VERSION = "${env.BUILD_PROPS.version}"
    }
    stages {
        stage ('Env health check and input sanitization') {
            steps {
                script {
                    if (env.VERSION == null) {
                        error('Version was not set or parsed properly.')
                    }
                    if (env.BRANCH_NAME == null) {
                        error('Branch name environment variable was incorrectly set.')
                    }
                    if (BUILD_TAG_PREFIX != '') {
                        BUILD_TAG_PREFIX += '-'
                    }
                    if (BUILD_TAG_SUFFIX != '') {
                        BUILD_TAG_SUFFIX = '-' + BUILD_TAG_SUFFIX
                    }
                    env.SERVER_IMAGE_FULL_NAME = "${DOCKER_REPO_LOCATION}/${DOCKER_REPO}${BUILD_TAG_PREFIX}ha-appserver${BUILD_TAG_SUFFIX}:${env.VERSION}-${env.BRANCH_NAME}"
                    env.FRONTEND_IMAGE_FULL_NAME = "${DOCKER_REPO_LOCATION}/${DOCKER_REPO}${BUILD_TAG_PREFIX}ha-appserver${BUILD_TAG_SUFFIX}:${env.VERSION}-${env.BRANCH_NAME}"
                    echo env.SERVER_IMAGE_FULL_NAME
                    echo env.FRONTEND_IMAGE_FULL_NAME
                }
                echo 'env: ' + sh (
                    script: 'env|sort',
                    returnStdout: true
                )
                container ('img-jdk17-gcloud') {
                    echo 'img version: ' + sh (
                        script: 'img --version',
                        returnStdout: true
                    )
                    echo 'java version: ' + sh (
                        script: 'java --version',
                        returnStdout: true
                    )
                    echo 'gcloud version: ' + sh (
                        script: 'gcloud --version',
                        returnStdout: true
                    )
                    echo 'python version: ' + sh (
                        script: 'python --version',
                        returnStdout: true
                    )
                    echo 'npm version: ' + sh (
                        script: 'npm --version',
                        returnStdout: true
                    )
                    // authenticate once within the container for the rest of the build
                    withCredentials ([file(credentialsId: 'hacmsa', variable: 'hacmsa')]) {
                        writeFile file: '~/hacmsa.json', text: readFile(hacmsa)
                    }
                    sh (
                        script: "gcloud auth activate-service-account --key-file=~/hacmsa.json --quiet && gcloud auth configure-docker ${DOCKER_REPO_LOCATION} --quiet"
                    )
                }
            }
        }
        stage ('Build artifacts') {
            steps {
                container ('img-jdk17-gcloud') {
                    dir ("${WORKSPACE}") {
                        sh (
                            script: './gradlew distribute'
                        )
                    }
                }
            }
        }
        stage ('Build docker images') {
            when {
                expression {
                    BRANCH_NAME.equals("release") || BRANCH_NAME.contains("cicdtest")
                }
            }
            steps {
                container ('img-jdk17-gcloud') {
                    dir ("${WORKSPACE}") {
                        echo 'Building server image...'
                        sh (
                            script: "img build . -f env/kubernetes/server/Dockerfile -t '${env.SERVER_IMAGE_FULL_NAME}' --build-arg artifactPath='dist/server/'"
                        )
                        echo 'Building frontend image...'
                        sh (
                            script: "img build . -f env/kubernetes/frontend/Dockerfile -t '${env.FRONTEND_IMAGE_FULL_NAME}' --build-arg artifactPath='frontend'"
                        )
                        echo 'Building loginserver image...'
//                         sh (
//                             script: ''
//                         )
                        echo 'Building action image...'
//                         sh (
//                             script: ''
//                         )
                    }
                }
            }
        }
        stage ('Push server docker image') {
            when {
                expression {
                    (BRANCH_NAME.equals("release") || BRANCH_NAME.contains("cicdtest")) || PUSH_IMAGES.contains('server')
                }
            }
            steps {
                container ('img-jdk17-gcloud') {
                    echo 'Pushing server image...'
                    sh (
                        script: "img push '${env.SERVER_IMAGE_FULL_NAME}'"
                    )
                }
            }
        }
        stage ('Push frontend docker image') {
            when {
                expression {
                    (BRANCH_NAME.equals("release") || BRANCH_NAME.contains("cicdtest")) || PUSH_IMAGES.contains('frontend')
                }
            }
            steps {
                container ('img-jdk17-gcloud') {
                    echo 'Pushing frontend image...'
                    sh (
                        script: "img push '${env.FRONTEND_IMAGE_FULL_NAME}'"
                    )
                }
            }
        }
        stage ('Push loginserver docker image') {
            when {
                expression {
                    (BRANCH_NAME.equals("release") || BRANCH_NAME.contains("cicdtest")) || PUSH_IMAGES.contains('loginserver')
                }
            }
            steps {
                container ('img-jdk17-gcloud') {
                    echo 'Pushing loginserver image...'
//                     sh (
//                         script: ''
//                     )
                }
            }
        }
        stage ('Push actionserver docker image') {
            when {
                expression {
                    (BRANCH_NAME.equals("release") || BRANCH_NAME.contains("cicdtest")) || PUSH_IMAGES.contains('actionserver')
                }
            }
            steps {
                container ('img-jdk17-gcloud') {
                    echo 'Pushing actionserver image...'
//                     sh (
//                         script: ''
//                     )
                }
            }
        }
    }
}
