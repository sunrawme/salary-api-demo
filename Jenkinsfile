
pipeline {
    agent any

    parameters {
        choice(
            name: 'ACTION',
            choices: ['DEPLOY', 'ROLLBACK'],
            description: 'Select deployment action'
        )

        string(
            name: 'TARGET_IMAGE',
            defaultValue: 'salary-api:build-7',
            description: 'Docker image to use for rollback'
        )
    }

    tools {
        jdk 'jdk17'
        maven 'maven3'
    }

    stages {
        stage('Verify Tools') {
            when {
                expression { params.ACTION == 'DEPLOY' }
            }
            steps {
                sh 'java -version'
                sh 'mvn -version'
            }
        }

        stage('Build Application') {
            when {
                expression { params.ACTION == 'DEPLOY' }
            }
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Deploy Application') {
            when {
                expression { params.ACTION == 'DEPLOY' }
            }
            steps {
                sh '''
                    set -e

                    ssh -i /var/lib/jenkins/.ssh/id_ed25519 \
                        -o BatchMode=yes \
                        ubuntu@192.168.73.218 \
                        "mkdir -p ~/salary-api-deploy"

                    scp -i /var/lib/jenkins/.ssh/id_ed25519 \
                        -o BatchMode=yes \
                        target/salary-api-1.0.0.jar \
                        Dockerfile \
                        ubuntu@192.168.73.218:salary-api-deploy/

                    ssh -i /var/lib/jenkins/.ssh/id_ed25519 \
                        -o BatchMode=yes \
                        ubuntu@192.168.73.218 \
                        "bash ~/salary-api-deploy/deploy.sh ${BUILD_NUMBER}"
                '''
            }
        }

        stage('Manual Rollback') {
            when {
                expression { params.ACTION == 'ROLLBACK' }
            }
            steps {
                sh '''
                    set -e

                    if ! echo "$TARGET_IMAGE" | grep -Eq '^salary-api:[a-zA-Z0-9._-]+$'; then
                        echo "Invalid Docker image name"
                        exit 1
                    fi

                    ssh -i /var/lib/jenkins/.ssh/id_ed25519 \
                        -o BatchMode=yes \
                        ubuntu@192.168.73.218 \
                        "bash ~/salary-api-deploy/manual-rollback.sh '$TARGET_IMAGE'"
                '''
            }
        }
    }

    post {
        success {
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true, allowEmptyArchive: true
            echo 'Pipeline completed successfully.'
        }

        failure {
            echo 'Pipeline failed. Check Console Output.'
        }
    }
}
