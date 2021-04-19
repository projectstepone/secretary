# secretary

How to start the secretary application
---

1. Run `mvn clean package` to build jar for application
1. Run docker-compose to launch service `docker-compose up --build -d`
1. Use Kafka client container to create topic
  `kafka-topics --create --zookeeper zookeeper:2181 --replication-factor 1 --partitions 2 --topic to-do-list`
  `kafka-topics --list --zookeeper zookeeper:2181`
  `kafka-topics --describe --zookeeper zookeeper:2181 --topic to-do-list`
1. Use Kafka client container to ingest messages in Kafka for topic
  `kafka-console-producer --broker-list broker:9092 --topic to-do-list --property "parse.key=true" --property "key.separator=:"`
1. Use Kafka client container to read messages from Kafka for topic
  `kafka-console-consumer --bootstrap-server broker:9092 --from-beginning --topic to-do-list --property "print.key=true"`

Health Check
---

To see your applications health enter url `http://localhost:8081/healthcheck`
