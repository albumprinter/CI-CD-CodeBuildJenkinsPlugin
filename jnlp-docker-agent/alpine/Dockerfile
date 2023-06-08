FROM jenkins/inbound-agent:3077.vd69cf116da_6f-3-alpine-jdk11
LABEL maintainer="Loren Segal <lsegal@soen.ca>"

# Simplify permissions in this containerized instance
USER root

# Install Docker
RUN apk add -U --no-cache docker

# Also install awscli
RUN apk add -U curl python3 python3-dev py-pip build-base
RUN pip install awscli --upgrade \
  && aws configure set default.region eu-west-1
# Setup jenkins-agent
COPY ./dockerd-entrypoint.sh /usr/local/bin/dockerd-entrypoint.sh
#RUN mv /usr/local/bin/jenkins-slave /usr/local/bin/jenkins-agent
RUN chmod +x /usr/local/bin/dockerd-entrypoint.sh

# Entrypoint
COPY ./entrypoint.sh /usr/local/bin/entrypoint.sh
RUN chmod +x /usr/local/bin/entrypoint.sh
ENTRYPOINT [ "/usr/local/bin/entrypoint.sh" ]
#ENTRYPOINT ["tail", "-f", "/dev/null"]

