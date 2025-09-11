FROM maven:3.9.11-eclipse-temurin-21 AS build

WORKDIR /workspace

COPY pom.xml ./

RUN mvn -B dependency:go-offline

COPY src ./src

RUN mvn -B clean package -DskipTests


FROM eclipse-temurin:21-jre

RUN groupadd -r dogwalks && useradd -r -g dogwalks -d /app -s /bin/bash dogwalks

WORKDIR /app

COPY --from=build /workspace/target/*.jar app.jar

RUN chown -R dogwalks:dogwalks /app

USER dogwalks

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java", "-jar", "app.jar"]