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

# AppCDS: aquece o classpath com H2 local (sem Supabase) e encerra após o contexto subir
RUN SPRING_PROFILES_ACTIVE=local \
    java -XX:ArchiveClassesAtExit=/workspace/app.jsa \
    -Dspring.context.exit=onRefresh \
    -jar /workspace/app.jar

# Camadas do fat JAR — melhor cache e ordem de carga no runtime
RUN java -Djarmode=tools -jar /workspace/app.jar extract \
    --layers --launcher --destination /workspace/extracted

# Runtime enxuto
FROM eclipse-temurin:25-jre-jammy AS runtime
WORKDIR /application

RUN groupadd --system chamego \
    && useradd --system --gid chamego --home-dir /application --shell /usr/sbin/nologin chamego

COPY --from=build --chown=chamego:chamego /workspace/app.jsa /application/app.jsa
COPY --from=build --chown=chamego:chamego /workspace/extracted/dependencies/ ./
COPY --from=build --chown=chamego:chamego /workspace/extracted/spring-boot-loader/ ./
COPY --from=build --chown=chamego:chamego /workspace/extracted/snapshot-dependencies/ ./
COPY --from=build --chown=chamego:chamego /workspace/extracted/application/ ./

USER chamego
EXPOSE 8080

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseSerialGC -XX:TieredStopAtLevel=1 -XX:+ExitOnOutOfMemoryError -XX:SharedArchiveFile=/application/app.jsa"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]