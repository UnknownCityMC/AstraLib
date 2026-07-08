pipeline {
    agent any
    tools {
        git 'Git'
        gradle 'Default'
        jdk 'jdk-21'
    }
    
    stages {
        stage('BuildAndPublish') {
            environment { 
                MVN_REPO_USERNAME = credentials('ReposiliteUser') 
                MVN_REPO_PASSWORD = credentials('ReposilitePassword')
            }
            steps {
                checkout scm
                sh "chmod +x gradlew"
                sh "./gradlew clean shadowJar --no-daemon"
                sh "./gradlew publish --no-daemon"
            }
            post {
                always {
                    sh '''#!/bin/bash
                            for i in astralib-paper-plugin/build/libs/*.jar
                            do mv "$i" "${i/.jar}-${BRANCH_NAME//\\//_}-#${BUILD_NUMBER}".jar
                            done  
                    '''
                    archiveArtifacts 'astralib-paper-plugin/build/libs/*.jar'
                }
            }
        }
    }
}
