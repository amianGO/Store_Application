# Dockerfile para desplegar el backend en Render
FROM eclipse-temurin:21-jdk-alpine

# Directorio de trabajo
WORKDIR /app

# Copiar archivos de Maven
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Dar permisos de ejecución a mvnw
RUN chmod +x ./mvnw

# Descargar dependencias
RUN ./mvnw dependency:go-offline

# Copiar código fuente
COPY src ./src

# Compilar la aplicación
RUN ./mvnw clean package -DskipTests

# Exponer el puerto (Render asigna dinámicamente el PORT)
EXPOSE 8080

# Ejecutar la aplicación con el perfil de producción
CMD ["java", "-Dspring.profiles.active=prod", "-jar", "target/inventory_app-0.0.1-SNAPSHOT.jar"]
