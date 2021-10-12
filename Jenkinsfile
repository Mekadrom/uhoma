pipeline {
    agent {
        node {
            label 'build-docker-java17-agent'
            customWorkspace 'workspace/home_assistant/'
        }
    }
    parameters {
        string(name: 'BUILD_TAG_PREFIX', defaultValue: '', description: 'A string to prefix the docker image tag names with. Optional.')
        string(name: 'BUILD_TAG_SUFFIX', defaultValue: '', description: 'A string to suffix the docker image tag names with. Optional.')
        string(name: 'DOCKER_REPO_PREFIX', defaultValue: 'us-east1-docker.pkg.dev/root-furnace-306909/hadocker-images/', description: 'The name of the docker repository in the HomeAssistant GCP project to push images to.')
        extendedChoice(defaultValue: 'appserver,frontend,loginserver,consumerserver', description: '', descriptionPropertyValue: 'appserver,frontend,loginserver,consumerserver', multiSelectDelimiter: ',', name: 'PUSH_IMAGES', quoteValue: false, saveJSONParameterToFile: false, type: 'PT_MULTI_SELECT', value: 'appserver,frontend,loginserver,consumerserver', visibleItemCount: 4)
    }
    environment {
        def props = readProperties  file: '${WORKSPACE}/gradle.properties'
        VERSION = props['version']
    }
    stages {
        stage ('Env health check and input sanitization') {
            steps {
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
            }
        }
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
            when {
                expression {
                    BRANCH_NAME.equals("release") || BRANCH_NAME.contains("cicdtest")
                }
            }
            steps {
                dir ("${WORKSPACE}/env/cloud/kubernetes") {
                    dir ('appserver') {
                        echo 'Building appserver image...'
                        sh (
                            script: "docker build . -t '${BUILD_TAG_PREFIX}ha-appserver${BUILD_TAG_SUFFIX}:${env.VERSION}-${env.BRANCH_NAME}' --build-arg artifactPath='${WORKSPACE}/dist/server/'"
                        )
                    }
                    dir ('frontend') {
                        echo 'Building frontend image...'
                        sh (
                            script: "docker build . -t '${BUILD_TAG_PREFIX}ha-frontend${BUILD_TAG_SUFFIX}:${env.VERSION}-${env.BRANCH_NAME}' --build-arg artifactPath='${WORKSPACE}/dist/frontend/'"
                        )
                    }
                    dir ('loginserver') {
                        echo 'Building loginserver image...'
                        sh (
                            script: ''
                        )
                    }
                    dir ('consumerserver') {
                        echo 'Building consumerserver image...'
                        sh (
                            script: ''
                        )
                    }
                }
            }
        }
        stage ('Push docker images') {
            when {
                expression {
                    BRANCH_NAME.equals("release") || BRANCH_NAME.contains("cicdtest")
                }
            }
            dir ("${WORKSPACE}/env/cloud/kubernetes") {
                dir ('appserver') {
                    when {
                        expression {
                            PUSH_IMAGES.contains('appserver')
                        }
                    }
                    echo 'Pushing appserver image...'
                    sh (
                        script: ''
                    )
                }
                dir ('frontend') {
                    when {
                        expression {
                            PUSH_IMAGES.contains('frontend')
                        }
                    }
                    echo 'Pushing frontend image...'
                    sh (
                        script: ''
                    )
                }
                dir ('loginserver') {
                    when {
                        expression {
                            PUSH_IMAGES.contains('loginserver')
                        }
                    }
                    echo 'Pushing loginserver image...'
                    sh (
                        script: ''
                    )
                }
                dir ('consumerserver') {
                    when {
                        expression {
                            PUSH_IMAGES.contains('consumerserver')
                        }
                    }
                    echo 'Pushing consumerserver image...'
                    sh (
                        script: ''
                    )
                }
            }
        }
    }
}
