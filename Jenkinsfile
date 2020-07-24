pipeline {
  agent {
    label 'java-1.8'
  }
  environment {
    ProjectName = 'CodeBuildJenkinsPlugin'
    MajorVersion = "1.0"
    Version = version(env.MajorVersion, env.BUILD_NUMBER, env.BRANCH_NAME)
  }
  stages {
    stage('Build') {          
      steps {
        script {
          sh "mvn clean install"
        }        
      }
    }
    stage('Publish') {
      steps {
        script {
          generic.publish(packageName: env.ProjectName, version: env.Version, pathToFile: "$workspace/target/codebuilder-cloud.hpi")
        }
      }    
    }
    stage('Promote to Release') {
      when {
        branch 'master'
      }
      steps {
        script {
          generic.promote(packageName: env.ProjectName, version: env.Version)
        }
      }
    }
  }
}