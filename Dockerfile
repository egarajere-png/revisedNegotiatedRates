ARG VERSION=0.0.1-SNAPSHOT
FROM openjdk:17
LABEL MAINTAINER "samuel.waithaka@abcthebank.com"

WORKDIR /app

COPY target/negotiatedrates-0.0.1-SNAPSHOT.jar ./

EXPOSE 8090
ENTRYPOINT ["java", "-jar", "./negotiatedrates-0.0.1-SNAPSHOT.jar"]