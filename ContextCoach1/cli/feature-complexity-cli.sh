#!/bin/bash

# Script to run the Feature Complexity Analysis CLI tool

# Get the directory where the script is located
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
ROOT_DIR="$SCRIPT_DIR/.."

# Change to the root directory
cd "$ROOT_DIR"

# Build the project if needed
echo "Building the project..."
./mvnw clean package -DskipTests

# Run the CLI tool
echo "Running Feature Complexity Analysis CLI..."
java -cp target/contextcoach-*.jar org.springframework.boot.loader.JarLauncher --spring.main.web-application-type=none --spring.main.banner-mode=off --spring.profiles.active=cli com.contextcoach.cli.FeatureComplexityCLI "$@"
