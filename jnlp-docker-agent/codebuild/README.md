# lsegal/jnlp-docker-agent:codebuild

This directory contains a `Dockerfile` that builds the `lsegal/jnlp-docker-agent`
image based on [`ubuntu:24.04`][ubuntu] with OpenJDK 17 and a full build
toolchain (Ant, Maven, Gradle, GitVersion, Docker and the AWS CLI v2), adding in
JNLP [remoting][remoting] support from [jenkins/inbound-agent][jnlpimage].

## Building `codebuild`

To build this image, run:

```sh
docker build -t lsegal/jnlp-docker-agent:codebuild .
```

[ubuntu]: https://hub.docker.com/_/ubuntu
[remoting]: https://github.com/jenkinsci/remoting
[jnlpimage]: https://hub.docker.com/r/jenkins/inbound-agent/
