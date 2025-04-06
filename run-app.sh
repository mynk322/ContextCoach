#!/bin/bash

# Function to handle cleanup on exit
cleanup() {
  echo "Shutting down services..."
  kill $BACKEND_PID $FRONTEND_PID 2>/dev/null
  exit 0
}

# Set up trap to catch Ctrl+C and other termination signals
trap cleanup SIGINT SIGTERM

# Check if MongoDB is installed
if command -v mongod &> /dev/null; then
  # MongoDB is installed, check if it's running
  echo "Checking MongoDB status..."
  if ! pgrep -x "mongod" > /dev/null; then
    echo "Starting MongoDB..."
    # Check if data directory exists
    if [ ! -d ~/data/db ]; then
      echo "Creating MongoDB data directory..."
      mkdir -p ~/data/db
    fi
    mongod --dbpath ~/data/db &
    MONGO_PID=$!
    echo "MongoDB started with PID: $MONGO_PID"
    # Give MongoDB time to start
    sleep 5
  else
    echo "MongoDB is already running."
  fi
else
  echo "MongoDB is not installed or not in your PATH."
  echo "Please install MongoDB or add it to your PATH and try again."
  echo "You can install MongoDB using:"
  echo "  - macOS: brew install mongodb-community"
  echo "  - Ubuntu: sudo apt install mongodb"
  echo "  - Windows: Download from https://www.mongodb.com/try/download/community"
  echo ""
  echo "For this run, we'll skip MongoDB and continue with the application."
  echo "Some features may not work properly without MongoDB."
  sleep 3
fi

# Start the Spring Boot backend
echo "Starting Spring Boot backend..."
cd ContextCoach1
# Create logs directory if it doesn't exist
mkdir -p logs

# Check if Maven wrapper exists
if [ -f "./mvnw" ]; then
  chmod +x ./mvnw
  ./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=8084" -Dspring-boot.run.main-class=com.contextcoach.ContextCoachApplication &
  BACKEND_PID=$!
  echo "Backend started with PID: $BACKEND_PID"
# Check if Maven is installed
elif command -v mvn &> /dev/null; then
  mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=8084" -Dspring-boot.run.main-class=com.contextcoach.ContextCoachApplication &
  BACKEND_PID=$!
  echo "Backend started with PID: $BACKEND_PID"
else
  echo "Maven wrapper (mvnw) not found and Maven is not installed or not in your PATH."
  echo "Please install Maven or add it to your PATH and try again."
  echo "You can install Maven using:"
  echo "  - macOS: brew install maven"
  echo "  - Ubuntu: sudo apt install maven"
  echo "  - Windows: Download from https://maven.apache.org/download.cgi"
  exit 1
fi

# Wait for backend to initialize with progress indicator
echo "Waiting for backend to initialize..."
for i in {1..30}; do
  echo -n "."
  sleep 1
done
echo ""
echo "Backend initialization time complete. Proceeding with frontend startup."

# Start the React frontend
echo "Starting React frontend..."
cd ../feature-clarify-analyze1
# Check if node_modules exists, if not run npm install
if [ ! -d "node_modules" ]; then
  echo "Node modules not found. Running npm install..."
  npm install
fi

npm run dev &
FRONTEND_PID=$!
echo "Frontend started with PID: $FRONTEND_PID"

echo "All services started. Access the application at http://localhost:3000"
echo "Press Ctrl+C to stop all services."

# Wait for any process to exit
wait $BACKEND_PID $FRONTEND_PID
# If we get here, one of the processes exited
echo "A process exited unexpectedly. Shutting down all services..."
cleanup
