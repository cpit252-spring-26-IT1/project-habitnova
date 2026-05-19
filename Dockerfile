FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

RUN groupadd -r habitnova && useradd -r -g habitnova habitnova

COPY --from=build /app/target/course-project-1.0-SNAPSHOT.jar app.jar

RUN chown -R habitnova:habitnova /app
USER habitnova

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=15s --retries=3 \
  CMD curl -f http://localhost:8080/ || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
