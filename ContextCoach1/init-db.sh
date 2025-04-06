#!/bin/bash

# Check if MongoDB is running
echo "Checking if MongoDB is running..."
if ! pgrep -x mongod > /dev/null; then
    echo "MongoDB is not running! Please start MongoDB with:"
    echo "  brew services start mongodb-community"
    echo "Or if using Docker:"
    echo "  docker run -d -p 27017:27017 --name mongodb mongo:latest"
    exit 1
fi

echo "MongoDB is running. You can now run the application with 'mvn spring-boot:run'" 