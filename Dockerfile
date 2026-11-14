# Build da API Spring Boot (Java 25)
FROM eclipse-temurin:25-jdk-jammy AS build
WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw

# Baixa dependências em camada separada (cache do Docker)
RUN ./mvnw -B -q -DskipTests dependency:go-offline || true

COPY src ./src
RUN ./mvnw -B -DskipTests package \
    && cp target/chamego-api-*.jar /workspace/app.jar

# Runtime enxuto
FROM eclipse-temurin:25-jre-jammy AS runtime
WORKDIR /app

RUN groupadd --system chamego \
    && useradd --system --gid chamego --home-dir /app --shell /usr/sbin/nologin chamego

COPY --from=build /workspace/app.jar /app/app.jar
RUN chown chamego:chamego /app/app.jar

USER chamego
EXPOSE 8080

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
