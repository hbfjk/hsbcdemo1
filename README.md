# Financial Transaction Service

- [Financial Transaction Service](#financial-transaction-service)
  - [Requirements](#requirements)
  - [Architecture](#architecture)
  - [How to Build and Run locally](#how-to-build-and-run-locally)
  - [How to Deploy on EKS:](#how-to-deploy-on-eks)
  - [API Documentation](#api-documentation)
    - [POST /api/accounts/{accountNumber}/balance](#post-apiaccountsaccountnumberbalance)
      - [Example:](#example)
      - [Response:](#response)
    - [POST /api/transactions/transfer](#post-apitransactionstransfer)
      - [Response:](#response-1)
      - [Response:](#response-2)
  - [Unit \& Integration Test](#unit--integration-test)
  - [Mock account and transaction data](#mock-account-and-transaction-data)
  - [Resilience Test](#resilience-test)
  - [Performance Test](#performance-test)
  - [TODO](#todo)


## Requirements

- OpenJDK 21.0.4
- Maven (3.8.7)
- Docker
- Kubernetes (AWS EKS)
- AWS ElastiCache Redis OSS (for caching)
- AWS RDS PostgreSQL (for database)

## Architecture


## How to Build and Run locally

1. Clone the repository:
```bash
git clone git@github.com:hbfjk/hsbcdemo1.git
cd hsbcdemo1
```

2. Build the project

Before run below command, make sure the postgresql database and redis server are setup properly which is specified in spring.redis and spring.datasrouce section src\main\resources\application.yml, create a hsbcdemo schema in the database.
```
mvn clean install
```
Or build the project with skipping test
```
mvn clean install -Dmaven.test.skip=true
```

3. Run the application locally:
```
mvn spring-boot:run
```

4. Run the application in the docker
```
mvn package
docker build -t transaction-service .
docker run -d -p 8081:8081 transaction-service
```
To run the docker connecting to AWS services, run below command with `-e SPRING_PROFILES_ACTIVE=default,prod`, make sure spring.redis and spring.datasrouce section are updated in src\main\resources\application`-prod`.yml in the build stage.
```
docker run -d -e SPRING_PROFILES_ACTIVE=default,prod -p 8081:8081 transaction-service
```

## How to Deploy on EKS:
1. Setup prerequisites:

- Set up AWS RDS PostgreSQL server，create a hsbcdemo schema in the database.
- Set up AWS ElastiCache Redis OSS
- Create an EKS cluster with 3 worker nodes, use the free t2.micro node type can reduce the cost.
- Push docker image to docker hub
```
docker login --username hbfjk
docker tag transaction-service hbfjk/transaction-service
docker push hbfjk/transaction-service
```

2. Connect to AWS EKS cluster:
- Create an IAM user in AWS console, with proper EKS cluster access permissions.
- Login with `aws configure` command, specifying AWS access key ID, AWS secret Access Key, Default region name(ap-southeast-2), Default output format(json)
```
aws configure
```
- Writing kubeconfig file with eksctl command
```
eksctl utils write-kubeconfig --region ap-southeast-2 --cluster test-cluster
```
- Verify kubectl works
```
> kubectl get node
NAME                                               STATUS   ROLES    AGE   VERSION
ip-172-31-11-97.ap-southeast-2.compute.internal    Ready    <none>   12h   v1.31.3-eks-59bf375
ip-172-31-35-223.ap-southeast-2.compute.internal   Ready    <none>   14h   v1.31.3-eks-59bf375
```
3. Deploy transaction application on EKS:

Replace `<user_name>` and `<user_password>` with your docker hub credentials
```
kubectl create secret docker-registry transaction-service-pull-secret \
  --docker-server=https://index.docker.io/v1/ \
  --docker-username=<user_name> \
  --docker-password=<user_password>

kubectl apply -f kubernetes/serviceaccount.yaml
kubectl apply -f kubernetes/deployment.yaml
kubectl apply -f kubernetes/service.yaml
kubectl apply -f kubernetes/hpa.yaml
```
4. Verify deployment:

- Verify 2 initial transaction service pods in ready state.
```
> kubectl get pod
NAME                                   READY   STATUS    RESTARTS   AGE
transaction-service-5597f5f775-69k8c   1/1     Running   0          65s
transaction-service-5597f5f775-zhm4r   1/1     Running   0          65s
```
- Verify service is associated with an external load banlancer with is specified in EXTERNAL-IP field.
```
> kubectl get svc
NAME                  TYPE           CLUSTER-IP       EXTERNAL-IP                                                                   PORT(S)        AGE
kubernetes            ClusterIP      10.100.0.1       <none>                                                                        443/TCP        3h15m
transaction-service   LoadBalancer   10.100.103.110   a46eba48ca66142aca515e9403848a3e-624959006.ap-southeast-2.elb.amazonaws.com   80:31909/TCP   7m25s
```
- Verify transaction service
```
curl http://a46eba48ca66142aca515e9403848a3e-624959006.ap-southeast-2.elb.amazonaws.com/actuator/health
```
It should return below response:
```
{"status":"UP","groups":["liveness","readiness"]}
```

## API Documentation
### POST /api/accounts/{accountNumber}/balance
- accountNumber: account number

#### Example:

```
curl -X GET http://localhost:8080/api/accounts/123456/balance
```

#### Response:

- 200 OK: 500.00
- 400 Bad Request: Account not found: 123456

### POST /api/transactions/transfer
- sourceAccountNumber: Source account number
- destinationAccountNumber: Destination account number
- amount: Amount to transfer

#### Response:

```
curl -X POST 'http://localhost:8081/api/transactions/transfer?sourceAccountNumber=12345&destinationAccountNumber=54321&amount=50'
```

#### Response:

- 200 OK: Transaction processed successfully
- 400 Bad Request: Transaction failed (e.g., insufficient funds)

## Unit & Integration Test
Run the unit tests with:
```
mvn clean test
```
The coverage report can be found in target/site/jacoco/index.html:
![](images/coverage_report.png)

## Mock account and transaction data
- Run below command to create 400 accounts(:mock-account-${n}) and 200 transactions between accounts randomly
```
curl --request POST '<load_balance_url>/api/mocks/mock?accountNumber=400&transactionNumber=200'

for i in {0..399}; do  balance=$(curl --silent --request GET "<load_balance_url>/api/accounts/mock-account-$i/balance");  echo "Account mock-account-$i balance：$balance"; done
```
- Verify mocked data

Verify above command return result like below:
```
Mock finished successfully
Account mock-account-0 balance：1722.40
Account mock-account-1 balance：3182.49
Account mock-account-2 balance：1068.38
Account mock-account-3 balance：3566.37
......
Account mock-account-399 balance：3512.36
```
Verify Redis have cache keys for the 400 accounts
```
> SCAN 0 MATCH accountCache:* COUNT 1000
1) "0"
2) 1) "accountCache::mock-account-237"
   2) "accountCache::mock-account-257"
   3) "accountCache::mock-account-349"
   4) "accountCache::mock-account-235"
......
   400) "accountCache::mock-account-15"
```

## Resilience Test
- Use Apache JMeter to perform load testing.

## Performance Test
- Use Apache JMeter to perform load testing.


## TODO
- Expose database and redis connection parameters as environment variable.
- Enable https secure port
- Provide front account search and transfer UI