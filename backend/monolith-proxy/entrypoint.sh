#!/bin/bash

# Set the path to the application JAR and migration directory
APP_JAR="app.jar"
FLYWAY_DIR="/app/flyway/sql"

#################################################################
# FUNCTIONS
#################################################################

# Function to check if an environment variable is set and not empty
check_env_var() {
    local var_name="$1"
    if [ -z "${!var_name}" ]; then
        echo "Error: Environment variable '$var_name' is not set or empty."
        exit 1
    fi
}

#################################################################
# ENVIRONMENT VARIABLES
#################################################################

# Comprehensive sanity checks for required environment variables
check_env_var "SPRING_APPLICATION_NAME"
check_env_var "SPRING_DATASOURCE_URL"
check_env_var "SPRING_DATASOURCE_USERNAME"
check_env_var "SPRING_DATASOURCE_PASSWORD"
check_env_var "JWT_SECRET"
check_env_var "KAFKA_USERNAME"
check_env_var "KAFKA_PASSWORD"



#################################################################
# FILE CHECKS
#################################################################

# Additional sanity checks
if [ ! -f "$APP_JAR" ]; then
    echo "Error: Application JAR '$APP_JAR' not found."
    exit 1
fi

# Ensure log directory exists
LOG_DIR="/app/logs"
mkdir -p "$LOG_DIR"


#################################################################
# JAVA APPLICATION STARTUP
#################################################################

# Enable Garbage Collection Logging
GC_LOG_OPTS="-Xlog:gc*:file=${LOG_DIR}/gc.log:time,uptimemillis:filecount=5,filesize=10M"

# Java options
JAVA_OPTS="-Dspring.application.name=$SPRING_APPLICATION_NAME"

# Start the Java application
exec java $JAVA_OPTS $GC_LOG_OPTS -jar "$APP_JAR"
