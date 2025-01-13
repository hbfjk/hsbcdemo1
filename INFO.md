## INFO
Architecture:
https://docs.aws.amazon.com/whitepapers/latest/scale-performance-elasticache/elasticache-for-redis.html

Delete all account caches in redis with cli 
```
EVAL "return redis.call('del', unpack(redis.call('keys', 'accountCache:*')))" 0
```
Run Docker with prod profile, TODO: expose DB and Redis configuration using environment。
```
mvn clean install
docker build -t transaction-service .
docker run -e TZ=Asia/Shanghai \
           -e SPRING_PROFILES_ACTIVE=default,prod \
           -p 8081:8081 transaction-service
```
Windows:
```
docker run -e TZ=Asia/Shanghai `
           -e SPRING_PROFILES_ACTIVE=default,prod `
           -p 8081:8081 transaction-service
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

Connect to EC2
```
ssh -i "fangjk-keypair.pem" ubuntu@ec2-54-206-165-130.ap-southeast-2.compute.amazonaws.com
```