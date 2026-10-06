FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw -B -q dependency:go-offline
COPY src src
RUN ./mvnw -B -q package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
RUN useradd --system --no-create-home appuser
COPY --from=build /workspace/target/job-tracker-*.jar app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "exec java -XX:MaxRAMPercentage=75 -Dserver.port=${PORT:-8080} -jar app.jar"]
