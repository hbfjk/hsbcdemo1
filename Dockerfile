FROM openjdk:21
LABEL maintainer="fangjieke@163.com"
COPY target/hsbcdemo1-1.0-SNAPSHOT.jar /app/transaction-service.jar
ENTRYPOINT ["java", "-jar", "/app/transaction-service.jar"]
EXPOSE 8081
CMD ["java", "-jar", "/app/transaction-service.jar"]
