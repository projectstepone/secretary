FROM ubuntu:18.04 AS base

RUN apt-get clean \
 && apt-get update \
 && apt-get install -y --no-install-recommends software-properties-common openjdk-8-jdk ca-certificates \
 && apt-get install -y --no-install-recommends ca-certificates-java bash curl tzdata iproute2 zip unzip wget

ADD target/secretary.jar server.jar
ADD scripts/startup.sh startup.sh

CMD ./startup.sh

FROM base as local

ADD config/docker.yml config/docker.yml

FROM base as prod

EXPOSE 8080
EXPOSE 8081
