FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/service-discovery-*.jar app.jar

RUN ln -snf /usr/share/zoneinfo/Europe/Moscow /etc/localtime && echo Europe/Moscow > /etc/timezone

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "/app/app.jar"]