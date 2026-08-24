pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        APP_IMAGE = 'todo-javaee'
        IMAGE_TAG = "build-${env.BUILD_NUMBER}"
        K8S_NAMESPACE = 'todo'
        KUBE_CONTEXT = 'minikube'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build and test') {
            steps {
                sh 'mvn -B clean verify'
            }
        }

        stage('Build Docker image') {
            steps {
                sh '''
                    docker build -t "$APP_IMAGE:$IMAGE_TAG" .
                    docker tag "$APP_IMAGE:$IMAGE_TAG" "$APP_IMAGE:minikube"
                '''
            }
        }

        stage('Load image into minikube') {
            steps {
                sh '''
                    minikube image load "$APP_IMAGE:$IMAGE_TAG"
                    minikube image load "$APP_IMAGE:minikube"
                '''
            }
        }

        stage('Deploy with Ansible') {
            steps {
                sh 'ansible-playbook ansible/deploy.yml -e image_tag="$IMAGE_TAG"'
            }
        }

        stage('Verify') {
            steps {
                sh '''
                    kubectl --context "$KUBE_CONTEXT" -n "$K8S_NAMESPACE" get pods
                    kubectl --context "$KUBE_CONTEXT" -n "$K8S_NAMESPACE" rollout status deployment/todo-javaee --timeout=300s

                    kubectl --context "$KUBE_CONTEXT" -n "$K8S_NAMESPACE" port-forward svc/todo-javaee 8085:80 > port-forward.log 2>&1 &
                    PF_PID=$!
                    sleep 12

                    curl -sf http://127.0.0.1:8085/api/todos
                    RC=$?

                    kill $PF_PID || true
                    exit $RC
                '''
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'target/*.war', allowEmptyArchive: true
        }
        failure {
            sh '''
                kubectl --context "$KUBE_CONTEXT" -n "$K8S_NAMESPACE" describe deployment todo-javaee || true
                kubectl --context "$KUBE_CONTEXT" -n "$K8S_NAMESPACE" get pods -o wide || true
            '''
        }
    }
}
