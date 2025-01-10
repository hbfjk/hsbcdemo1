# Financial Transaction Service

## Overview

This service processes financial transactions, updates account balances in real-time, and ensures that accounts never have negative balances.

## Features

- Process transactions with unique IDs, source, and destination accounts, amount, and timestamp.
- Prevent negative account balances.
- High availability with Kubernetes.
- Retry mechanism for failed transactions.
- Caching with Redis for performance.

## Requirements

- JDK 11 or later
- Docker
- Kubernetes cluster (AWS EKS, GCP GKE, Alibaba ACK)
- Redis for caching

## Setup

1. Clone the repository.
2. Build the application with Maven: `mvn clean install`.
3. Run the application locally: `mvn spring-boot:run`.
4. Deploy to Kubernetes with `kubectl apply -f kubernetes/`.

## Testing

- Unit tests: `mvn test`
- Integration tests: Test database and cache interactions.

## Architecture

- **API Gateway**: Exposes REST endpoints.
- **Transaction Service**: Processes transactions and updates account balances.
- **Database**: MySQL (RDS or any managed database).
- **Caching**: Redis for high-frequency transactions.
- **Kubernetes**: High availability with HPA for scalability.