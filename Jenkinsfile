pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub_accessToken')
        DOCKER_IMAGE_NAME = "hanseonghyeon/kefa"
    }

    stages {
        stage('CI') {
            when {
                anyOf {
                    changeRequest target: 'develop'
                    changeRequest target: 'main'
                }
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
                anyOf {
                    allOf {
                        branch 'develop'             // develop 브랜치일 때
                        not { changeRequest() }      // PR이 아닐 때 (즉, 머지 후)
                    }
                    allOf {
                        branch 'main'                // main 브랜치일 때
                        not { changeRequest() }      // PR이 아닐 때 (즉, 머지 후)
                    }
                }
            }
            stages {
                stage('Build') {
                    steps {
                        checkout scm
                        sh 'chmod +x ./gradlew'
                        sh './gradlew clean build'
                        sh 'ls -la build/libs/'
                    }
                }

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
                                string(credentialsId: 'SMEE_TARGET', variable: 'SMEE_TARGET'),
                                string(credentialsId: 'JWT_KEY', variable: 'JWT_KEY'),
                                string(credentialsId: 'JWT_ACCESS_EXPIRATION_TIME', variable: 'JWT_ACCESS_EXPIRATION_TIME'),
                                string(credentialsId: 'JWT_REFRESH_EXPIRATION_TIME', variable: 'JWT_REFRESH_EXPIRATION_TIME'),
                                string(credentialsId: 'MAIL_USERNAME', variable: 'MAIL_USERNAME'),
                                string(credentialsId: 'MAIL_PASSWORD', variable: 'MAIL_PASSWORD'),
                                string(credentialsId: 'GOOGLE_CLIENT_ID', variable: 'GOOGLE_CLIENT_ID'),
                                string(credentialsId: 'GOOGLE_CLIENT_SECRET', variable: 'GOOGLE_CLIENT_SECRET'),
                                string(credentialsId: 'KAKAO_CLIENT_ID', variable: 'KAKAO_CLIENT_ID'),
                                string(credentialsId: 'KAKAO_CLIENT_SECRET', variable: 'KAKAO_CLIENT_SECRET'),
                                string(credentialsId: 'OAUTH2_MAIN_PAGE_URI', variable: 'OAUTH2_MAIN_PAGE_URI'),
                                string(credentialsId: 'CIPHER_SECRET_KEY', variable: 'CIPHER_SECRET_KEY'),
                                string(credentialsId: 'CIPHER_IV', variable: 'CIPHER_IV'),
                                string(credentialsId: 'NTS_API_KEY', variable: 'NTS_API_KEY'),
                                string(credentialsId: 'NTS_BASE_URL', variable: 'NTS_BASE_URL')
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
                                    echo "JWT_KEY=${JWT_KEY}" >> .env
                                    echo "JWT_ACCESS_EXPIRATION_TIME=${JWT_ACCESS_EXPIRATION_TIME}" >> .env
                                    echo "JWT_REFRESH_EXPIRATION_TIME=${JWT_REFRESH_EXPIRATION_TIME}" >> .env
                                    echo "MAIL_USERNAME=${MAIL_USERNAME}" >> .env
                                    echo "MAIL_PASSWORD=${MAIL_PASSWORD}" >> .env
                                    echo "GOOGLE_CLIENT_ID=${GOOGLE_CLIENT_ID}" >> .env
                                    echo "GOOGLE_CLIENT_SECRET=${GOOGLE_CLIENT_SECRET}" >> .env
                                    echo "KAKAO_CLIENT_ID=${KAKAO_CLIENT_ID}" >> .env
                                    echo "KAKAO_CLIENT_SECRET=${KAKAO_CLIENT_SECRET}" >> .env
                                    echo "OAUTH2_MAIN_PAGE_URI=${OAUTH2_MAIN_PAGE_URI}" >> .env
                                    echo "CIPHER_SECRET_KEY=${CIPHER_SECRET_KEY}" >> .env
                                    echo "CIPHER_IV=${CIPHER_IV}" >> .env
                                    echo "NTS_API_KEY=${NTS_API_KEY}" >> .env
                                    echo "NTS_BASE_URL=${NTS_BASE_URL}" >> .env
                                '''
                            }
                        }
                    }
                }

                stage('Build and Push') {
                    steps {
                        script {
                            // 현재 디렉토리 및 파일 확인
                            sh 'pwd'
                            sh 'ls -la'
                            sh 'ls -la build/libs/'

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
            cleanWs()
        }
        success {
            echo 'Pipeline completed successfully'
        }
        failure {
            echo 'Pipeline failed. Please check the logs'
        }
    }
}
