pipeline {
    agent any

    tools {
        gradle 'gradle'
    }

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub_accessToken')
        DOCKER_IMAGE_NAME = "hanseonghyeon/kefa"
    }

    stages {
        stage('CI') {
            when {
                changeRequest target: 'develop'
            }
            steps {
                checkout scm
                sh 'chmod +x ./gradlew'
                sh './gradlew clean test'
                sh './gradlew build'
            }
        }

        stage('CD') {
            when {
                allOf {
                    branch 'develop'
                    not {
                        changeRequest()
                    }
                }
            }
            stages {
                stage('Environment Setup') {
                    steps {
                        script {
                            withCredentials([
                                string(credentialsId: 'DB_HOST', variable: 'DB_HOST'),
                                string(credentialsId: 'DB_NAME', variable: 'DB_NAME'),
                                string(credentialsId: 'DB_USER', variable: 'DB_USER'),
                                string(credentialsId: 'DB_PASSWORD', variable: 'DB_PASSWORD'),
                                string(credentialsId: 'DB_PORT', variable: 'DB_PORT'),
                                string(credentialsId: 'REDIS_HOST', variable: 'REDIS_HOST'),
                                string(credentialsId: 'REDIS_PORT', variable: 'REDIS_PORT'),
                                string(credentialsId: 'SERVER_PORT', variable: 'SERVER_PORT'),
                                string(credentialsId: 'JENKINS_PORT', variable: 'JENKINS_PORT'),
                                string(credentialsId: 'JENKINS_AGENT_PORT', variable: 'JENKINS_AGENT_PORT'),
                                string(credentialsId: 'TZ', variable: 'TZ'),
                                string(credentialsId: 'SMEE_URL', variable: 'SMEE_URL'),
                                string(credentialsId: 'SMEE_TARGET', variable: 'SMEE_TARGET')
                            ]) {
                                sh '''
                                    echo "DB_HOST=${DB_HOST}" > .env
                                    echo "DB_NAME=${DB_NAME}" >> .env
                                    echo "DB_USER=${DB_USER}" >> .env
                                    echo "DB_PASSWORD=${DB_PASSWORD}" >> .env
                                    echo "DB_PORT=${DB_PORT}" >> .env
                                    echo "REDIS_HOST=${REDIS_HOST}" >> .env
                                    echo "REDIS_PORT=${REDIS_PORT}" >> .env
                                    echo "SERVER_PORT=${SERVER_PORT}" >> .env
                                    echo "JENKINS_PORT=${JENKINS_PORT}" >> .env
                                    echo "JENKINS_AGENT_PORT=${JENKINS_AGENT_PORT}" >> .env
                                    echo "TZ=${TZ}" >> .env
                                    echo "SMEE_URL=${SMEE_URL}" >> .env
                                    echo "SMEE_TARGET=${SMEE_TARGET}" >> .env
                                '''
                            }
                        }
                    }
                }

                stage('Build and Push') {
                    steps {
                        script {
                            sh 'echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'
                            sh "docker build -t ${DOCKER_IMAGE_NAME}:${BUILD_NUMBER} ."
                            sh "docker tag ${DOCKER_IMAGE_NAME}:${BUILD_NUMBER} ${DOCKER_IMAGE_NAME}:latest"
                            sh "docker push ${DOCKER_IMAGE_NAME}:${BUILD_NUMBER}"
                            sh "docker push ${DOCKER_IMAGE_NAME}:latest"
                        }
                    }
                }

//                 stage('Deploy') {    원격으로 실제 서버에 접속해 배포 작업 실행 할 수 있음
//                     steps {
//                         script {
//                             sh "ssh user@dev-server 'docker pull ${DOCKER_IMAGE_NAME}:latest && docker-compose up -d'"
//                         }
//                     }
//                 }
            }
        }
    }

    post {
        always {
            sh 'rm -f .env'
            sh 'docker logout'
            sh 'docker image prune -f'
        }
        success {
            echo 'Pipeline completed successfully'
        }
        failure {
            echo 'Pipeline failed. Please check the logs'
        }
    }
}
