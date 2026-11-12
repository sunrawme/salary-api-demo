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

        stage('Deploy with Automatic Rollback') {
            steps {
                sh '''
                    set -e

                    echo "Preparing deployment directory..."

                    ssh -i /var/lib/jenkins/.ssh/id_ed25519 \
                        -o BatchMode=yes \
                        ubuntu@192.168.73.218 \
                        "mkdir -p ~/salary-api-deploy"

                    echo "Copying application files..."

                    scp -i /var/lib/jenkins/.ssh/id_ed25519 \
                        -o BatchMode=yes \
                        target/salary-api-1.0.0.jar \
                        Dockerfile \
                        ubuntu@192.168.73.218:salary-api-deploy/

                    echo "Starting remote deployment..."

                    ssh -i /var/lib/jenkins/.ssh/id_ed25519 \
                        -o BatchMode=yes \
                        ubuntu@192.168.73.218 \
                        "bash ~/salary-api-deploy/deploy.sh"
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
