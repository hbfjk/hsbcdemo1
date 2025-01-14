## INFO

Initiate ubuntu machine:
```
sudo apt update
sudo apt install openjdk-21-jdk
sudo apt install maven
sudo apt install docker.io
sudo apt install redis-tools

git clone git@github.com:hbfjk/hsbcdemo1.git
```

Architecture:
https://docs.aws.amazon.com/whitepapers/latest/scale-performance-elasticache/elasticache-for-redis.html

Delete all account caches in redis with cli 
```
EVAL "return redis.call('del', unpack(redis.call('keys', 'accountCache:*')))" 0
```
Run Docker with prod profile, TODO: expose DB and Redis configuration using environment。
```
mvn clean install package
docker build -t transaction-service .
docker run -d -e SPRING_PROFILES_ACTIVE=default,prod -p 80:8081 transaction-service
```

mock and transfer:
```
curl --location --request GET 'localhost:80/actuator'
curl --location --request POST 'localhost:80/api/mocks/mock?accountNumber=200&transactionNumber=100'
curl --location --request POST 'localhost:80/api/transactions/transfer?sourceAccountNumber=mock-account-0&destinationAccountNumber=mock-account-1&amount=50'
```

curl --location --request POST 'http://a46eba48ca66142aca515e9403848a3e-624959006.ap-southeast-2.elb.amazonaws.com//api/mocks/mock?accountNumber=2000&transactionNumber=1000'

Connect to EC2
```
ssh -i "fangjk-keypair.pem" ubuntu@ec2-54-206-71-83.ap-southeast-2.compute.amazonaws.com
```

Connecting to Redis
https://medium.com/@rajeevpmr/remote-redis-connection-using-stunnel-5817ad6861ff
https://www.digitalocean.com/community/tutorials/how-to-set-up-an-ssl-tunnel-using-stunnel-on-ubuntu
https://www.digitalocean.com/community/tutorials/how-to-connect-to-managed-redis-over-tls-with-stunnel-and-redis-cli
https://docs.aws.amazon.com/AmazonElastiCache/latest/dg/accessing-elasticache.html
https://www.youtube.com/watch?v=wp1QnBW2kIM
https://github.com/avizway1/aws-projects/blob/main/redis.md
```
redis-cli -h master.fangjk-redis.7paiac.apse2.cache.amazonaws.com -p 6379 -a zhu88jiezhu88jie --tls
```
find all account cache
```
keys *
SCAN 0 MATCH accountCache:* COUNT 1000
EVAL "for _,key in ipairs(redis.call('keys', 'accountCache:*')) do redis.call('del', key) end" 0
```

Connecting to PostgreSQL:
```

```


AWS CLI:
```
PS C:\Users\fangj> aws configure sso
SSO session name (Recommended): my-sso
SSO start URL [None]: https://d-9767b7a5f3.awsapps.com/start
SSO region [None]: ap-southeast-2
SSO registration scopes [sso:account:access]:
Attempting to automatically open the SSO authorization page in your default browser.
If the browser does not open or you wish to use a different device to authorize this request, open the following URL:

https://oidc.ap-southeast-2.amazonaws.com/authorize?response_type=code&client_id=Rbq9iK_qO8QE_gRycSUwFmFwLXNvdXRoZWFzdC0y&redirect_uri=http%3A%2F%2F127.0.0.1%3A59353%2Foauth%2Fcallback&state=627ba982-48a4-4ef3-8494-46dc1970439c&code_challenge_method=S256&scopes=sso%3Aaccount%3Aaccess&code_challenge=L4ikJbvBAKLd6fK3Kxs6ufbYVBmdFJvJYiGszIaR5J0
The only AWS account available to you is: 864981728728
Using the account ID 864981728728
The only role available to you is: AdministratorAccess
Using the role name "AdministratorAccess"
CLI default client Region [None]: ap-southeast-2
CLI default output format [None]: json
CLI profile name [AdministratorAccess-864981728728]: my-dev-profile

To use this profile, specify the profile name using --profile, as shown:
aws s3 ls --profile my-dev-profile
```
Login with AWS CLI:
```
aws sso login --profile my-dev-profile

aws configure
eksctl utils write-kubeconfig --region ap-southeast-2 --cluster test-cluster

aws eks get-token --region ap-southeast-2 --cluster-name test-cluster


```

```
eksctl create cluster `
--name test-cluster `
--version 1.31 `
--region ap-southeast-2 `
--nodegroup-name linux-nodes `
--node-type t2.micro `
--nodes 2 --profile my-dev-profile
```

```
eksctl delete cluster --name test-cluster
```

access key: AKIA4SZHNXXMAVT7KHYK/zhKUtuHXr73ALbNyUCMegTNLoP4LPVSehCOQ084H

