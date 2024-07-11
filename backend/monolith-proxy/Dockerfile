FROM maven:3.9.6-amazoncorretto-17-al2023 AS build
WORKDIR /app

COPY pom.xml ./
RUN mvn dependency:go-offline -B
COPY src ./src
# Since we using test containers we cannot run it within docker
RUN mvn clean package -DskipTests


FROM amazoncorretto:17.0.10-al2023-headless
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
# Java VisualVM Profiler
EXPOSE 9010 
ENTRYPOINT ["java", "-Dcom.sun.management.jmxremote", \
            "-Dcom.sun.management.jmxremote.port=9010", \
            "-Dcom.sun.management.jmxremote.rmi.port=9010", \
            "-Dcom.sun.management.jmxremote.authenticate=false", \
            "-Dcom.sun.management.jmxremote.ssl=false", \
            "-Djava.rmi.server.hostname=localhost", \
            "-jar", "app.jar"]

            
# ENTRYPOINT ["java", "-jar", "app.jar"]
