# Copyright 2017-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# Licensed under the Amazon Software License (the "License"). You may not use this file except in compliance with the License.
# A copy of the License is located at
#
#    http://aws.amazon.com/asl/
#
# or in the "license" file accompanying this file.
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, express or implied.
# See the License for the specific language governing permissions and limitations under the License.
#

# Jenkins remoting (agent.jar + jenkins-agent launcher) is copied from the
# official inbound-agent image rather than rebuilt here.
FROM jenkins/inbound-agent:latest-jdk17 AS build

FROM ubuntu:24.04

ENV DEBIAN_FRONTEND=noninteractive \
    JAVA_HOME="/usr/lib/jvm/java-17-openjdk-amd64" \
    ANT_VERSION=1.10.17 \
    ANT_DOWNLOAD_SHA512="a96ca6455ec9af5e702df7d1ed5a2ec1cfb381eac3e9a767ecb6be1706e826941045e8fd7ca663ba101bc47bfa18351f540dd27e9e7af57908ac78e65268eee7" \
    MAVEN_HOME="/opt/maven" \
    MAVEN_VERSION=3.9.16 \
    MAVEN_CONFIG="/root/.m2" \
    MAVEN_DOWNLOAD_SHA512="831a8591fe20c8243b1dbe7d71e3244f31d1665b0804b2e825e38cbbe5ce0cafb8338851f90780735568773e0a6cd07bbec107cda0b896b008b861075358b6f6" \
    GRADLE_VERSION=9.6.0 \
    GRADLE_DOWNLOAD_SHA256="bbaeb2fef8710818cf0e261201dab964c572f92b942812df0c3620d62a529a01" \
    GITVERSION_VERSION=6.7.0 \
    GITVERSION_DOWNLOAD_SHA256="e2012abfb3392a6f0ccf5f82738173c291b15bfdb2d5e9197777ec3f41b88f76"

# Install git, SSH, a build toolchain, common build-time libraries, Python 3
# and OpenJDK 17. Package versions are no longer pinned: the Ubuntu archive is
# GPG-signed, so apt verifies integrity, and pinning frees us from chasing the
# exact point releases that ship in each Ubuntu version.
RUN set -ex \
    && apt-get update \
    && apt-get install -y --no-install-recommends \
        ca-certificates curl wget gnupg lsb-release \
        git openssh-client \
        build-essential autoconf automake libtool pkg-config make patch file \
        unzip zip tar gzip bzip2 xz-utils \
        python3 python3-pip python3-venv python3-setuptools python3-dev \
        fakeroot imagemagick subversion \
        openjdk-17-jdk ca-certificates-java \
        libssl-dev libbz2-dev libcurl4-openssl-dev libdb-dev libevent-dev \
        libffi-dev libgeoip-dev libglib2.0-dev libjpeg-dev libkrb5-dev \
        liblzma-dev libmagickcore-dev libmagickwand-dev default-libmysqlclient-dev \
        libncurses-dev libpng-dev libpq-dev libreadline-dev libsqlite3-dev \
        libwebp-dev libxml2-dev libxml2-utils libxslt1-dev libyaml-dev zlib1g-dev \
    && update-ca-certificates -f \
    && git version \
    && mkdir -p ~/.ssh \
    && touch ~/.ssh/known_hosts \
    && ssh-keyscan -t rsa,ecdsa,ed25519 -H github.com >> ~/.ssh/known_hosts \
    && ssh-keyscan -t rsa,ecdsa,ed25519 -H bitbucket.org >> ~/.ssh/known_hosts \
    && chmod 600 ~/.ssh/known_hosts \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Install Docker (engine + CLI + buildx + compose) from Docker's signed apt
# repository. This provides dockerd for the docker-in-docker entrypoint.
RUN set -ex \
    && install -m 0755 -d /etc/apt/keyrings \
    && curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc \
    && chmod a+r /etc/apt/keyrings/docker.asc \
    && echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo "$VERSION_CODENAME") stable" > /etc/apt/sources.list.d/docker.list \
    && apt-get update \
    && apt-get install -y --no-install-recommends \
        docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin \
    && docker -v \
    # Keep the legacy `docker-compose` command working by shimming it onto the v2 plugin
    && printf '#!/bin/sh\nexec docker compose "$@"\n' > /usr/local/bin/docker-compose \
    && chmod +x /usr/local/bin/docker-compose \
    # set up subuid/subgid so that "--userns-remap=default" works out-of-the-box
    && groupadd dockremap \
    && useradd -g dockremap dockremap \
    && echo 'dockremap:165536:65536' >> /etc/subuid \
    && echo 'dockremap:165536:65536' >> /etc/subgid \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Download and set up GitVersion (self-contained binary, no Mono required)
RUN set -ex \
    && curl -fSL "https://github.com/GitTools/GitVersion/releases/download/${GITVERSION_VERSION}/gitversion-linux-x64-${GITVERSION_VERSION}.tar.gz" -o /tmp/gitversion.tar.gz \
    && echo "${GITVERSION_DOWNLOAD_SHA256}  /tmp/gitversion.tar.gz" | sha256sum -c - \
    && tar -xzf /tmp/gitversion.tar.gz -C /usr/local/bin gitversion \
    && chmod +x /usr/local/bin/gitversion \
    && rm /tmp/gitversion.tar.gz

# Install Ant, Maven and Gradle from their official distributions
RUN set -ex \
    # Ant
    && curl -fSL "https://archive.apache.org/dist/ant/binaries/apache-ant-${ANT_VERSION}-bin.tar.gz" -o /tmp/ant.tar.gz \
    && echo "${ANT_DOWNLOAD_SHA512}  /tmp/ant.tar.gz" | sha512sum -c - \
    && tar -xzf /tmp/ant.tar.gz -C /opt \
    && update-alternatives --install /usr/bin/ant ant /opt/apache-ant-${ANT_VERSION}/bin/ant 10000 \
    # Maven
    && mkdir -p "${MAVEN_HOME}" \
    && curl -fSL "https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz" -o /tmp/maven.tar.gz \
    && echo "${MAVEN_DOWNLOAD_SHA512}  /tmp/maven.tar.gz" | sha512sum -c - \
    && tar -xzf /tmp/maven.tar.gz -C "${MAVEN_HOME}" --strip-components=1 \
    && update-alternatives --install /usr/bin/mvn mvn "${MAVEN_HOME}/bin/mvn" 10000 \
    && mkdir -p "${MAVEN_CONFIG}" \
    # Gradle
    && curl -fSL "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" -o /tmp/gradle.zip \
    && echo "${GRADLE_DOWNLOAD_SHA256}  /tmp/gradle.zip" | sha256sum -c - \
    && unzip -q /tmp/gradle.zip -d /opt \
    && update-alternatives --install /usr/local/bin/gradle gradle /opt/gradle-${GRADLE_VERSION}/bin/gradle 10000 \
    && rm -f /tmp/ant.tar.gz /tmp/maven.tar.gz /tmp/gradle.zip

# Install the AWS CLI v2
RUN set -ex \
    && curl -fSL "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o /tmp/awscliv2.zip \
    && unzip -q /tmp/awscliv2.zip -d /tmp \
    && /tmp/aws/install \
    && rm -rf /tmp/aws /tmp/awscliv2.zip \
    && aws --version

VOLUME /var/lib/docker

COPY dockerd-entrypoint.sh /usr/local/bin/

COPY m2-settings.xml $MAVEN_CONFIG/settings.xml

# Jenkins Agent setup
COPY --from=build /usr/local/bin/jenkins-agent /usr/bin/jenkins-agent
COPY --from=build /usr/share/jenkins/agent.jar /usr/share/jenkins/agent.jar
RUN mkdir -p /home/jenkins/.jenkins /home/jenkins/agent \
    && chmod +x /usr/bin/jenkins-agent /usr/local/bin/dockerd-entrypoint.sh
WORKDIR /home/jenkins/agent
