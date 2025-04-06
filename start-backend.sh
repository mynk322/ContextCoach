#!/bin/bash

# Start the Spring Boot backend
echo "Starting Spring Boot backend..."
cd ContextCoach1
# Create logs directory if it doesn't exist
mkdir -p logs

# Check if Maven wrapper exists
if [ -f "./mvnw" ]; then
  chmod +x ./mvnw
  ./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=8084"
else
  echo "Maven wrapper not found. Please make sure you're in the correct directory."
  exit 1
fi 