FROM maven:3.9.9-eclipse-temurin-11 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn -DskipTests dependency:go-offline

COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:11-jre
WORKDIR /app

RUN addgroup --system spring && adduser --system spring --ingroup spring

COPY --from=build /app/target/java-mvc-thymeleaf-0.0.1-SNAPSHOT.jar app.jar

ENV APP_SEED_ENABLED=true
ENV JAVA_OPTS=""

EXPOSE 8080

USER spring:spring

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]