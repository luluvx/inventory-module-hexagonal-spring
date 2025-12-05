# =============================================
# Dockerfile para el modulo de Inventario
# =============================================

# Etapa 1: Build
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Copiar archivos de Maven
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Dar permisos de ejecucion al wrapper
RUN chmod +x mvnw

# Descargar dependencias (cache de Docker)
RUN ./mvnw dependency:go-offline -B

# Copiar codigo fuente
COPY src src

# Compilar la aplicacion
RUN ./mvnw package -DskipTests -B

# =============================================
# Etapa 2: Runtime
# =============================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Crear usuario no-root para seguridad
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copiar el JAR desde la etapa de build
COPY --from=builder /app/target/*.jar app.jar

# Cambiar al usuario no-root
USER appuser

# Variables de entorno configurables por sucursal
ENV BRANCH_SLUG=""
ENV SERVER_PORT=8080
ENV SPRING_DATASOURCE_URL="jdbc:postgresql://postgres-db:5432/inventario"
ENV SPRING_DATASOURCE_USERNAME="postgres"
ENV SPRING_DATASOURCE_PASSWORD="root"
ENV SPRING_JPA_HIBERNATE_DDL_AUTO="update"

# Puerto expuesto
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:${SERVER_PORT}/actuator/health || exit 1

# Comando de inicio
ENTRYPOINT ["java", "-jar", "app.jar"]
