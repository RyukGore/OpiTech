# Etapa 1: construir el JAR con Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build

#set working directory
WORKDIR /app

# Copiar pom y código fuente
COPY pom.xml .
COPY src ./src

# Compilar y empaquetar (salida: target/*.jar)
RUN mvn clean package -DskipTests

# Etapa 2: imagen ligera solo con el JAR
FROM eclipse-temurin:21-jre

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el JAR desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto en el que corre Spring Boot (ajusta si cambiaste server.port)
EXPOSE 5300

# Comando de inicio
ENTRYPOINT ["java", "-jar", "/app/app.jar"]