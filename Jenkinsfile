pipeline {
    agent {
        kubernetes {
            yaml """
              apiVersion: v1
              kind: Pod
              spec:
                containers:
                - name: docker
                  image: docker:20.10.12-dind
                  securityContext:
                    privileged: true
                  command:
                  - cat
                  tty: true
                  env:
                  - name: DOCKER_TLS_CERTDIR
                    value: ""
                - name: aws-kubectl
                  image: amazon/aws-cli:latest
                  command:
                  - cat
                  tty: true
            """
        }
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

        stage('Setup kubectl') {
            steps {
                container('aws-kubectl') {
                    sh '''
                        curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
                        chmod +x kubectl
                        mv kubectl /usr/local/bin/
                        kubectl version --client
                    '''
                }
            }
        }

        stage('Build & Push Docker Image') {
            steps {
                container('docker') {  // 이 부분이 중요!
                    script {
                        def imageTag = "build-${env.BUILD_NUMBER}"

                        // Docker daemon이 시작될 때까지 대기
                        sh 'dockerd-entrypoint.sh &'
                        sh 'sleep 10'
                        sh 'docker --version'

                        withAWS(credentials: 'aws-credentials', region: "${AWS_DEFAULT_REGION}") {
                            // AWS CLI가 docker 컨테이너에 없으므로 다른 방법 사용
                            sh "docker build -t ${ECR_IMAGE_URI}:${imageTag} ."

                            // ECR 로그인을 aws-kubectl 컨테이너에서 수행
                        }
                    }
                }

                // ECR 로그인과 푸시를 분리
                container('aws-kubectl') {
                    withAWS(credentials: 'aws-credentials', region: "${AWS_DEFAULT_REGION}") {
                        script {
                            def imageTag = "build-${env.BUILD_NUMBER}"
                            sh """
                                # ECR 로그인 토큰 가져오기
                                aws ecr get-login-password --region ${AWS_DEFAULT_REGION} > /tmp/ecr-password
                            """
                        }
                    }
                }

                container('docker') {
                    script {
                        def imageTag = "build-${env.BUILD_NUMBER}"
                        sh """
                            # ECR 로그인
                            cat /tmp/ecr-password | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com

                            # 이미지 푸시
                            docker push ${ECR_IMAGE_URI}:${imageTag}
                            docker tag ${ECR_IMAGE_URI}:${imageTag} ${ECR_IMAGE_URI}:latest
                            docker push ${ECR_IMAGE_URI}:latest
                        """
                    }
                }
            }
        }

        stage('Deploy to EKS') {
            steps {
                container('aws-kubectl') {
                    script {
                        withAWS(credentials: 'aws-credentials', region: "${AWS_DEFAULT_REGION}") {
                            sh """
                                aws eks update-kubeconfig --region ${AWS_DEFAULT_REGION} --name your-cluster-name
                                kubectl set image deployment/${K8S_DEPLOYMENT_NAME} \
                                        ${ECR_REPOSITORY_NAME}=${ECR_IMAGE_URI}:latest \
                                        -n ${K8S_NAMESPACE}
                                kubectl rollout status deployment/${K8S_DEPLOYMENT_NAME} -n ${K8S_NAMESPACE}
                            """
                        }
                    }
                }
            }
        }
    }
}