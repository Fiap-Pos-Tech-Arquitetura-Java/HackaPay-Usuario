FROM ubuntu:latest AS build

RUN sudo apt-get update
RUN sudo apt-get install openjdk-17-jdk -y
COPY . .

RUN sudo pt-get install maven -y
RUN sudo apt-get install -y git

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