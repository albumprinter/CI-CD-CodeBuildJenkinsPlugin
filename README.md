# CI-CD-CodeBuildJenkinsPlugin

## Resources

[Build][buildlink] | [Artifacts][artifactslink]

[buildlink]: https://jenkins.albelli.com/job/CICD/job/CI-CD-CodeBuildJenkinsPlugin/
[artifactslink]: https://artifactory.albelli.com/ui/repos/tree/General/generic-dev-local%2FCodeBuildJenkinsPlugin

## Dependencies

To build this project locally, you will need the following dependencies:

- JDK Version: 1.8.0
- Maven Version: 3.6.3

## How to update Jenkins

In order to make a new version of this plugin available on Jenkins, you will need to change the Jenkins master Dockerfile in the **CICD-BuildEnvironmentImages** repository.

## License

This project is a fork of [Loren Segal's CodeBuilder Jenkins Plugin](https://github.com/lsegal/jenkins-codebuilder-plugin) and is
licensed under the MIT license.
