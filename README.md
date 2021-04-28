# Secretary : File upload service

Local run of application
---

- Run `mvn clean package` to build jar for application
- Run docker-compose to launch service
```
cd secretary-server
docker-compose up --build -d
```
- Use Kafka client container to create topic
 ```
  kafka-topics --create --zookeeper zookeeper:2181 --replication-factor 1 --partitions 2 --topic secretary.filedata.random
  kafka-topics --list --zookeeper zookeeper:2181
  kafka-topics --describe --zookeeper zookeeper:2181 --topic secretary.filedata.random
```
- Use Kafka client container to read messages from Kafka for topic
```
kafka-console-consumer --bootstrap-server broker:9092 --from-beginning --topic secretary.filedata.random --property "print.key=true"
```
