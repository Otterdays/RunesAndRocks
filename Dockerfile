FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copy the Gradle wrapper and configuration files first to cache dependencies
COPY gradlew gradlew.bat ./
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY shared/build.gradle.kts shared/
COPY server/build.gradle.kts server/
COPY client/build.gradle.kts client/
COPY android/build.gradle.kts android/

# Copy the entire source tree and build the server distribution
COPY . .
RUN ./gradlew :server:installDist --no-daemon

# -----------
# Production Image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Expose Ktor TCP game server and Admin WebUI ports
EXPOSE 25565
EXPOSE 8080

# Copy the built distribution from the builder stage
COPY --from=builder /app/server/build/install/server /app/server

# Ensure the executable has run permissions
RUN chmod +x /app/server/bin/server

# Set environment variables so the server attempts to connect to the Docker compose hostnames
ENV REDIS_HOST=runes_redis
ENV REDIS_PORT=6379
ENV JDBC_URL=jdbc:postgresql://runes_postgres:5432/runesandrocks
ENV DB_USER=runes_admin
ENV DB_PASS=runes_password
ENV PORT=25565

ENTRYPOINT ["/app/server/bin/server"]
