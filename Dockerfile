FROM ubuntu:latest AS build

RUN apt-get update
RUN apt-get install openjdk-17-jdk -y
COPY . .

RUN apt-get install maven -y
RUN apt-get install git -y --fix-missing

RUN mkdir /hackapay-security
RUN git clone https://github.com/Fiap-Pos-Tech-Arquitetura-Java/Hackapay-Security /hackapay-security
WORKDIR /hackapay-security
RUN mvn clean install

WORKDIR /
RUN mvn clean install

FROM openjdk:17-jdk-slim

EXPOSE 8080

COPY --from=build /target/HackaPay-Usuario-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]