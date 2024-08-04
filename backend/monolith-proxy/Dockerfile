# Stage 1: Build the application
ARG TARGETPLATFORM
FROM --platform=${TARGETPLATFORM:-linux/arm64} maven:3.9.6-amazoncorretto-17-al2023 AS build

WORKDIR /app

COPY pom.xml ./
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Prepare the runtime environment
FROM --platform=${TARGETPLATFORM:-linux/arm64} openjdk:17-jdk-slim as base

ENV APP_HOME=/app
ENV FLYWAY_VERSION=10.17.0
ENV PATH=$PATH:$APP_HOME/flyway-$FLYWAY_VERSION

RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    curl \
    tar \
    netcat && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

WORKDIR $APP_HOME

RUN curl -L https://download.red-gate.com/maven/release/com/redgate/flyway/flyway-commandline/$FLYWAY_VERSION/flyway-commandline-$FLYWAY_VERSION-linux-x64.tar.gz | tar xz

COPY --from=build /app/target/*.jar app.jar

COPY entrypoint.sh $APP_HOME/start-app.sh
COPY ./src/main/resources/db/migration $APP_HOME/flyway/sql

RUN chmod +x /app/flyway-10.17.0/flyway
RUN chmod +x $APP_HOME/start-app.sh

RUN mkdir -p /app/logs && chmod -R 777 /app/logs

# Switch to non-root user
USER 1000

EXPOSE 8080

ENTRYPOINT ["/app/start-app.sh"]
