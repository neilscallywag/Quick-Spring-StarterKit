FROM maven:3.9.6-amazoncorretto-17-al2023 AS build
WORKDIR /app

COPY pom.xml ./
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests


FROM amazoncorretto:17.0.10-al2023-headless
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
