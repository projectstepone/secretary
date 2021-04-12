# secretary

How to start the secretary application
---

1. Run `mvn clean package` to build jar for application
2. Run docker-compose to launch service `docker-compose up --build -d`
1. To check that your application is running enter url `http://localhost:8080/secretary`

Health Check
---

To see your applications health enter url `http://localhost:8081/healthcheck`
