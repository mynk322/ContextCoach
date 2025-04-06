# Vector Database Integration

This document describes the integration of the vector database functionality from the `convertToVectorDB` project into the ContextCoach application.

## Overview

The vector database functionality allows the ContextCoach application to search for relevant code snippets in a repository based on a query. This is useful for the feature complexity analysis, as it can help identify similar code patterns and estimate the complexity of a new feature.

## Components

The integration consists of the following components:

1. **PythonVectorDB**: A Java implementation of the `VectorDB` interface that communicates with the Python vector database API.
2. **vector-db-setup.sh**: A shell script that sets up the vector database by starting the API server and converting a repository to vector embeddings.
3. **Python scripts**: The Python scripts from the `convertToVectorDB` project that implement the vector database functionality.

## Setup

To set up the vector database, follow these steps:

1. Make sure Python 3 is installed on your system.
2. Run the `vector-db-setup.sh` script:

```bash
./vector-db-setup.sh [repository-url]
```

This script will:
- Install the required Python dependencies
- Start the vector database API server
- Convert the specified repository (or the ContextCoach repository by default) to vector embeddings
- Set the necessary environment variables for the Java application

## Usage

To use the vector database in the ContextCoach application, set the following environment variables:

```bash
export USE_REAL_VECTOR_DB=true
export VECTOR_DB_API_URL=http://localhost:5000
```

Then run the feature complexity CLI:

```bash
./feature-complexity-cli.sh
```

The application will use the vector database to search for relevant code snippets when analyzing the feature complexity.

## Implementation Details

### PythonVectorDB

The `PythonVectorDB` class implements the `VectorDB` interface and communicates with the Python vector database API. It has the following features:

- Automatically starts the vector database API server if it's not running
- Converts queries to vector embeddings and searches for similar code snippets
- Returns formatted code snippets with metadata (file path, similarity score)

### Vector Database API

The vector database API is implemented in Python and provides the following endpoints:

- `/health`: Check if the API server is running
- `/stats`: Get statistics about the vector database
- `/query`: Search for code snippets based on a query embedding
- `/add_documents`: Add documents to the vector database
- `/clear`: Clear the vector database

### Vector Embeddings

The vector embeddings are generated using the `sentence-transformers` library in Python. The default model is `all-MiniLM-L6-v2`, which provides a good balance between performance and accuracy.

## Testing

The integration includes unit tests for the `PythonVectorDB` class, which mock the API calls to the Python vector database.

## Future Improvements

- Add support for multiple repositories
- Improve the search algorithm to consider code structure and semantics
- Add a web interface for browsing and searching the vector database
- Integrate with the main ContextCoach application for more advanced features
