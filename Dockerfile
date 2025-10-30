
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app


COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN chmod +x mvnw || true
RUN ./mvnw -q -DskipTests dependency:go-offline


COPY src ./src
RUN ./mvnw -q -DskipTests package


FROM eclipse-temurin:17-jre
WORKDIR /app

RUN useradd -ms /bin/bash appuser \
 && mkdir -p /app/data \
 && chown -R appuser:appuser /app

USER appuser

COPY --from=build /app/target/*.jar /app/app.jar

ENV SPRING_DATASOURCE_URL=jdbc:sqlite:/app/data/app.db
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS=""

EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
