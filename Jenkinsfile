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
                  volumeMounts:
                  - name: shared-volume
                    mountPath: /shared
                - name: aws-kubectl
                  image: amazon/aws-cli:latest
                  command:
                  - cat
                  tty: true
                  volumeMounts:
                  - name: shared-volume
                    mountPath: /shared
                volumes:
                - name: shared-volume
                  emptyDir: {}
            """
        }
    }

    environment {
        AWS_ACCOUNT_ID      = "956463122808"
        AWS_DEFAULT_REGION  = "ap-northeast-2"
        ECR_REPOSITORY_NAME = "frontend"
        K8S_DEPLOYMENT_NAME = "frontend-deployment"
        K8S_NAMESPACE       = "default"
        ECR_IMAGE_URI       = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com/${ECR_REPOSITORY_NAME}"
        EKS_CLUSTER_NAME    = "aivle-5-eks"
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
                container('docker') {
                    script {
                        def imageTag = "build-${env.BUILD_NUMBER}"

                        sh 'dockerd-entrypoint.sh &'
                        sh 'sleep 10'
                        sh 'docker --version'
                        sh "docker build -t ${ECR_IMAGE_URI}:${imageTag} ."
                        sh "docker tag ${ECR_IMAGE_URI}:${imageTag} ${ECR_IMAGE_URI}:latest"
                    }
                }

                // ECR 로그인 토큰을 공유 볼륨에 저장
                container('aws-kubectl') {
                    withAWS(credentials: 'aws-credentials', region: "${AWS_DEFAULT_REGION}") {
                        sh """
                            aws ecr get-login-password --region ${AWS_DEFAULT_REGION} > /shared/ecr-password
                        """
                    }
                }

                // Docker push
                container('docker') {
                    script {
                        def imageTag = "build-${env.BUILD_NUMBER}"
                        sh """
                            # ECR 로그인 (공유 볼륨에서 패스워드 읽기)
                            cat /shared/ecr-password | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com

                            # 이미지 푸시
                            docker push ${ECR_IMAGE_URI}:${imageTag}

                            # 정리
                            rm -f /shared/ecr-password
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
                                aws eks update-kubeconfig --region ${AWS_DEFAULT_REGION} --name ${EKS_CLUSTER_NAME}

                                kubectl set image deployment/${K8S_DEPLOYMENT_NAME} \
                                        ${ECR_REPOSITORY_NAME}=${ECR_IMAGE_URI}:${imageTag} \
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