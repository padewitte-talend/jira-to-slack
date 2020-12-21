#!/usr/bin/env groovy

GString POD_LABEL = "jira-to-slack-build-${UUID.randomUUID().toString()}"

pipeline {

    agent {
        kubernetes {
            label POD_LABEL
            yaml """
apiVersion: v1
kind: Pod
spec:
  imagePullSecrets:
    - talend-registry
  containers:
    - name: tsbi-builder
      image: artifactory.datapwn.com/tlnd-docker-prod/talend/common/tsbi/jdk11-svc-springboot-builder:2.5.3-2.3-20201201131449
      command:
      - cat
      tty: true
      resources:
          requests:
            memory: "5120Mi"
            cpu: "2.0"
          limits:
            memory: "5120Mi"
            cpu: "2.0"
      volumeMounts:
      - name: docker
        mountPath: /var/run/docker.sock
      - name: m2
        mountPath: /root/.m2/repository
  volumes:
  - name: docker
    hostPath:
      path: /var/run/docker.sock
  - name: m2
    hostPath:
      path: /tmp/jenkins/tmc/m2
  """
        }
    }
    
    options {
        timeout(time: 120, unit: 'MINUTES')
        skipStagesAfterUnstable()
    }


    stages {

        stage('Tbuild') {
            steps {

                container('tsbi-builder') {
                    withCredentials([
                            usernamePassword(credentialsId: 'artifactory-datapwn-credentials', passwordVariable: 'JFROG_DOCKER_PASSWORD', usernameVariable: 'JFROG_DOCKER_LOGIN')
                    ]) {
                        sh '''#! /bin/bash
                    cd jira-to-slack
                    echo "## Building jira-to-slack ##"
                    echo ${JFROG_DOCKER_PASSWORD} | docker login ${DOCKER_REGISTRY} -u ${JFROG_DOCKER_LOGIN} --password-stdin
                    # mvn build and docker push -Dspring-boot.build-image.publish=true 
                    mvn -T 1C -Dcheckstyle.skip -Dpmd.skip=true  -Dmaven.test.skip=true   spring-boot:build-image
                    DOCKER_IMAGE_VERSION=$(mvn org.apache.maven.plugins:maven-help-plugin:3.2.0:evaluate -Dexpression=project.version -q -DforceStdout)
                    DOCKER_IMAGE="artifactory.datapwn.com/tlnd-docker-dev/talend/management-console/tmc-qa/talend-slack-bots/jirabot:$DOCKER_IMAGE_VERSION"
                    docker push $DOCKER_IMAGE                  
                    '''
                    }
                }
            }
        }
    }

}