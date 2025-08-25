pipeline {
    agent any
    tools {
        dockerTool 'docker'
    }

    environment {
        AWS_ACCOUNT_ID      = "956463122808"
        AWS_DEFAULT_REGION  = "ap-northeast-2"
        ECR_REPOSITORY_NAME = "operation"
        K8S_DEPLOYMENT_NAME = "operation-deployment"
        K8S_NAMESPACE       = "default"
        ECR_IMAGE_URI = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com/${ECR_REPOSITORY_NAME}"
    }

    stages {
        stage('Checkout Source') {
            steps {
                checkout scm
            }
        }

        stage('Build & Push Docker Image') {
            steps {
                script {
                    def imageTag = "build-${env.BUILD_NUMBER}"
                    sh 'docker --version'

                    withAWS(credentials: 'aws-credentials', region: "${AWS_DEFAULT_REGION}") {
                        sh "docker build -t ${ECR_IMAGE_URI}:${imageTag} ."
                        sh "aws ecr get-login-password --region ${AWS_DEFAULT_REGION} | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com"
                        sh "docker push ${ECR_IMAGE_URI}:${imageTag}"
                        sh "docker tag ${ECR_IMAGE_URI}:${imageTag} ${ECR_IMAGE_URI}:latest"
                        sh "docker push ${ECR_IMAGE_URI}:latest"
                    }
                }
            }
        }

        stage('Deploy to EKS') {
            steps {
                script {
                    echo "EKS에 최신 이미지를 배포합니다..."
                    sh """
                    kubectl set image deployment/${K8S_DEPLOYMENT_NAME} \
                            *= ${ECR_IMAGE_URI}:latest \
                            -n ${K8S_NAMESPACE}
                    """
                }
            }
        }
    }
}