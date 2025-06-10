
# Usar a imagem base do Java 17
FROM openjdk:17-jdk-slim

# Definir o diretório de trabalho dentro do container
WORKDIR /app

# Copiar os arquivos do projeto para dentro do container
COPY . .

# Construir o projeto (se for Maven)
RUN ./mvnw package -DskipTests

# Definir o comando para rodar a aplicação
CMD ["java", "-jar", "target/api-payment-authentication-0.0.1-SNAPSHOT.jar"]