# Secretary : File upload service

Motivation
---

Without this service, bulk ingestion of data in Statesman service (*state transition service*) is performed using scripts.
To ingest data via these scripts, someone with access to relevant production machines has to run the scripts manually.

In an ideal scenario, operations team should be able to ingest this data to Statesman without access to production machines.
To enable this, Secretary has been created. It will parse the file, perform sanity checks on data in it, post which data will be ingested to Statesman.


Design
---

TODO : ADD DETAILS

Major architectural decisions have been recorded in [ADRs](doc/architecture/decisions/toc.md)

Local setup
---

- Run `mvn clean package` to build jar for application

- Run docker-compose to launch service
```
cd secretary-server

# Force build of Secretary image before creating container
docker-compose up --build -d 
```

- Use Kafka client container to create topic
 ```
# Create topic in Kafka; confirm topic with service configuration file
kafka-topics --create --zookeeper zookeeper:2181 --replication-factor 1 --partitions 2 --topic secretary.filedata.random

# Get list of all topics in Kafka
kafka-topics --list --zookeeper zookeeper:2181

# Get details about topic from Kafka
kafka-topics --describe --zookeeper zookeeper:2181 --topic secretary.filedata.random
```

- Use Kafka client container to read messages from Kafka for topic
```
kafka-console-consumer --bootstrap-server broker:9092 --from-beginning --topic secretary.filedata.random --property "print.key=true"
```

- JDB can connect to local instance of Secretary for debug. Port can be found in `docker-compose.yml`
