/**
 * Copyright © 2018 Thomas Biesaart (thomas.biesaart@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
pipeline {
    agent {
        dockerfile {
            dir 'buildagent'
        }
    }

    stages {
        stage('Check License Headers') {
            steps {
                mvn 'license:check'
            }
        }
        stage('Build & Test') {
            steps {
                script {
                    docker.image("rabbitmq:management").withRun() { rabbitMq ->
                        sh "echo ${rabbitMq.Id} and ${rabbitMq.id}"
                        mvn 'org.jacoco:jacoco-maven-plugin:prepare-agent install'
                    }
                }
            }
        }
        stage('Analyze') {
            steps {
                mvn 'sonar:sonar -Dsonar.branch.name="$BRANCH_NAME"'
            }
        }
    }
}

void mvn(String args) {
    configFileProvider([configFile(fileId: '0d067750-aa03-49f8-a998-49f8c7b6cdff', variable: 'MVN_SETTINGS_FILE')]) {
        sh "mvn --settings \"$MVN_SETTINGS_FILE\" $args"
    }
}