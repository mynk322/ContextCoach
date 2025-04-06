#!/bin/bash

# Start the React frontend
echo "Starting React frontend..."
cd feature-clarify-analyze1
# Check if node_modules exists, if not run npm install
if [ ! -d "node_modules" ]; then
  echo "Node modules not found. Running npm install..."
  npm install
fi

npm run dev 