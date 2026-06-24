# lsegal/jnlp-docker-agent:alpine

This directory contains a `Dockerfile` that builds the `lsegal/jnlp-docker-agent`
image based on [jenkins/inbound-agent:alpine-jdk17][jnlpimage], adding in Docker
and AWS CLI support.

## Building `alpine`

To build this image, run:

```sh
docker build -t lsegal/jnlp-docker-agent:alpine .
```

[jnlpimage]: https://hub.docker.com/r/jenkins/inbound-agent/
