# Financial Transaction Service

## Requirements

- JDK 11+
- Docker
- Kubernetes (AWS EKS, GCP GKE, or Alibaba ACK)
- Redis (for caching)
- PostgreSQL (for database)

## How to Build and Run

1. Clone the repository:
```bash
git clone <repo-url>
cd financial-transaction-service
```

2. Build the project
```
mvn clean install
```

3. Run the application locally:
```
mvn spring-boot:run
```

4. Deploy to Kubernetes (AWS EKS example):
```
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
kubectl apply -f k8s/hpa.yaml
```

5. Docker
```
docker build -t transaction-service .
docker run -p 8081:8081 transaction-service
```

## API Documentation
### POST /api/transactions/transfer
- sourceAccountNumber: Source account number
- destinationAccountNumber: Destination account number
- amount: Amount to transfer

#### Response:

- 200 OK: Transaction processed successfully
- 400 Bad Request: Transaction failed (e.g., insufficient funds)

## Testing
Run the unit tests with:
```
mvn test
```

## Performance Testing
- Use Apache JMeter to perform load testing.










