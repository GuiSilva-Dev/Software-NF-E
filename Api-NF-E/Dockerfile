# Etapa 1 - Build Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

# Etapa 2 - Runtime
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

RUN mkdir -p /app/Banco_de_dados/pdfs_salvos

COPY --from=build /app/target/*.jar api-recibos.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "api-recibos.jar"]