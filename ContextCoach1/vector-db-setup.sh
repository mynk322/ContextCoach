#!/bin/bash

# Vector Database Setup Script
# This script sets up the vector database for the ContextCoach project

# Check if Python is installed
if ! command -v python3 &> /dev/null; then
    echo "Python 3 is required but not installed. Please install Python 3 and try again."
    exit 1
fi

# Set paths
VECTOR_DB_DIR="$HOME/Desktop/workspace/convertToVectorDB"
REPO_URL=${1:-"https://github.com/mayankpadia/ContextCoach.git"}
API_URL="http://localhost:5000"

# Check if the vector database directory exists
if [ ! -d "$VECTOR_DB_DIR" ]; then
    echo "Vector database directory not found at $VECTOR_DB_DIR"
    exit 1
fi

# Install Python dependencies
echo "Installing Python dependencies..."
cd "$VECTOR_DB_DIR"
pip3 install -r requirements.txt

# Start the vector database API server in the background
echo "Starting vector database API server..."
python3 "$VECTOR_DB_DIR/mock_vector_db_api.py" --host 127.0.0.1 --port 5000 &
API_PID=$!

# Wait for the API server to start
echo "Waiting for API server to start..."
sleep 5

# Check if the API server is running
if ! curl -s "http://localhost:5000/health" > /dev/null; then
    echo "Failed to start API server"
    exit 1
fi

echo "API server started successfully"

# Convert the repository to vector embeddings
echo "Converting repository to vector embeddings..."
python3 "$VECTOR_DB_DIR/repo_to_vector.py" --repo-url "$REPO_URL" --api-url "$API_URL"

# Check if the conversion was successful
if [ $? -ne 0 ]; then
    echo "Failed to convert repository to vector embeddings"
    exit 1
fi

echo "Repository converted to vector embeddings successfully"

# Set environment variables for the Java application
export USE_REAL_VECTOR_DB=true
export VECTOR_DB_API_URL="$API_URL"

echo "Vector database setup completed successfully"
echo "Environment variables set:"
echo "USE_REAL_VECTOR_DB=$USE_REAL_VECTOR_DB"
echo "VECTOR_DB_API_URL=$VECTOR_DB_API_URL"

echo "To use the vector database in your application, run:"
echo "export USE_REAL_VECTOR_DB=true"
echo "export VECTOR_DB_API_URL=$API_URL"

echo "To run the feature complexity CLI with the vector database, use:"
echo "./feature-complexity-cli.sh"

# Keep the API server running
echo "Press Ctrl+C to stop the API server"
wait $API_PID
