FROM openjdk:11

ADD target/secretary.jar server.jar
ADD scripts/startup.sh startup.sh
ADD config/docker.yml docker.yml

EXPOSE 8080

EXPOSE 8081

CMD ./startup.sh