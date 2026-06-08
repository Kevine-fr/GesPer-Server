# syntax=docker/dockerfile:1.6

# ─── Build stage ────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

# Maven utilise le wrapper si présent, sinon on l'installe.
RUN apk add --no-cache maven=~3.9

# Cache des dépendances Maven (couche optimisée)
COPY pom.xml ./
RUN mvn -B -e -ntp dependency:go-offline

# Build
COPY src ./src
RUN mvn -B -e -ntp clean package -DskipTests \
 && cp target/*.jar /app/app.jar

# ─── Runtime stage ──────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime

LABEL org.opencontainers.image.title="GesPer Server" \
      org.opencontainers.image.description="API REST de gestion financière personnelle" \
      org.opencontainers.image.source="https://github.com/gesper/gesper-server"

# Utilisateur non-root
RUN addgroup -S gesper && adduser -S gesper -G gesper

WORKDIR /app
COPY --from=build /app/app.jar /app/app.jar

# Healthcheck via Actuator
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD wget -qO- http://localhost:8080/api/v1/actuator/health | grep -q '"status":"UP"' || exit 1

USER gesper
EXPOSE 8080

# Options JVM optimisées pour container (heap dynamique)
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
