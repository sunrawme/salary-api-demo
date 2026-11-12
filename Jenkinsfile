
pipeline {
    agent any

    tools {
        jdk 'jdk17'
        maven 'maven3'
    }

    stages {
        stage('Verify Tools') {
            steps {
                sh 'java -version'
                sh 'mvn -version'
            }
        }

        stage('Build Application') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Deploy to Docker Server') {
            steps {
                sh '''
                    ssh -i /var/lib/jenkins/.ssh/id_ed25519 \
                    -o BatchMode=yes \
                    ubuntu@192.168.73.218 \
                    "mkdir -p ~/salary-api-deploy/target"

                    scp -i /var/lib/jenkins/.ssh/id_ed25519 \
                    target/salary-api-1.0.0.jar \
                    ubuntu@192.168.73.218:salary-api-deploy/target/

                    scp -i /var/lib/jenkins/.ssh/id_ed25519 \
                    Dockerfile \
                    ubuntu@192.168.73.218:salary-api-deploy/

                   
                    ssh -i /var/lib/jenkins/.ssh/id_ed25519 \
                    -o BatchMode=yes \
                    ubuntu@192.168.73.218 '
                        set -e
                        cd ~/salary-api-deploy
                        docker build -t salary-api:v2 .
                        docker rm -f salary-container 2>/dev/null || true
                        docker run -d --name salary-container \
                        -p 8080:8080 salary-api:v2
'

                '''
            }
        }
    }

    post {
        success {
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
        }
    }
}
