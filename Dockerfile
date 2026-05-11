# Build stage
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app

# Copiar wrapper de Maven y el POM
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
# Asegurar permisos de ejecución para el wrapper
RUN chmod +x mvnw

# Descargar dependencias (mejora el cache de Docker)
RUN ./mvnw dependency:go-offline -B

# Copiar el código fuente y compilar
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Run stage
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# Crear usuario no-root para ejecutar la aplicación
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiar el .jar compilado desde el stage anterior
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto por defecto de Spring Boot
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
