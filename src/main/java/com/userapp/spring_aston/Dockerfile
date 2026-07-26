# Базовый образ
FROM openjdk:17-jdk-slim

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем JAR файл
COPY target/user-service-*.jar app.jar

# Копируем файл с настройками времени (опционально)
RUN ln -snf /usr/share/zoneinfo/Europe/Moscow /etc/localtime && echo Europe/Moscow > /etc/timezone

# Открываем порт
EXPOSE 8081

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "/app/app.jar"]