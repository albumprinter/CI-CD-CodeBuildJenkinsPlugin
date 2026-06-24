FROM jenkins/inbound-agent:alpine-jdk17
LABEL maintainer="Loren Segal <lsegal@soen.ca>"

# Simplify permissions in this containerized instance
USER root

# Install Docker (for docker-in-docker builds) and the AWS CLI v2, both from
# the Alpine community repository.
RUN apk add -U --no-cache docker aws-cli

# Setup docker-in-docker entrypoint helper. The inbound-agent image already
# provides /usr/local/bin/jenkins-agent, so no rename is needed.
COPY ./dockerd-entrypoint.sh /usr/local/bin/dockerd-entrypoint.sh
RUN chmod +x /usr/local/bin/dockerd-entrypoint.sh

# Entrypoint
COPY ./entrypoint.sh /usr/local/bin/entrypoint.sh
RUN chmod +x /usr/local/bin/entrypoint.sh
ENTRYPOINT [ "/usr/local/bin/entrypoint.sh" ]
