
# Build the app
FROM maven:3.9.11-eclipse-temurin-24 AS build

WORKDIR /workspace

COPY pom.xml ./

RUN mvn -B -DskipTests dependency:go-offline

COPY src ./src

RUN mvn clean package -DskipTests

# Run the app
FROM eclipse-temurin:24-jre

WORKDIR /app

COPY --from=build /workspace/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]