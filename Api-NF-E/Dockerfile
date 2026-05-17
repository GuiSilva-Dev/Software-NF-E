# 1. Imagem oficial do Java 17 super leve
FROM eclipse-temurin:21-jdk-alpine

# 2. Cria a pasta para o banco de dados e PDFs dentro do container
RUN mkdir -p /app/Banco_de_dados/pdfs_salvos

# 3. Copia o arquivo .jar que o Maven acabou de criar
COPY target/*.jar /app/api-recibos.jar

# 4. Define a pasta de trabalho
WORKDIR /app

# 5. O comando que dá o "Play" na sua API
ENTRYPOINT ["java", "-jar", "api-recibos.jar"]