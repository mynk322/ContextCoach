# Program Manager AI

This project consists of two main components:
1. **ContextCoach** - A Spring Boot backend application
2. **feature-clarify-analyze** - A React/TypeScript frontend application

## Prerequisites

- Java 17 or higher
- Node.js 16 or higher
- MongoDB (optional, but recommended for full functionality)
- Maven (if Maven wrapper is not available)

### Troubleshooting Prerequisites

If you encounter issues with the run script:

1. **MongoDB not found**: The script will warn you if MongoDB is not installed or not in your PATH. You can install MongoDB using:
   ```
   # For macOS with Homebrew
   brew tap mongodb/brew
   brew install mongodb-community
   ```

2. **Maven not found**: If the Maven wrapper (`mvnw`) is not available in the ContextCoach directory and Maven is not installed, you'll need to install Maven:
   ```
   # For macOS with Homebrew
   brew install maven
   ```

3. **Multiple main classes**: The project contains multiple main classes. The main class is now specified in the pom.xml file, but when running manually, you can also specify it:
   ```
   mvn spring-boot:run -Dspring-boot.run.main-class=com.contextcoach.ContextCoachApplication
   ```

4. **JPA Configuration**: The application uses MongoDB and not JPA. JPA dependencies have been removed and auto-configuration has been disabled to prevent errors related to missing database drivers.

## Setup

### MongoDB Setup

Make sure MongoDB is installed and running. If you don't have MongoDB set up, follow these steps:

1. Install MongoDB:
   ```
   # For macOS with Homebrew
   brew tap mongodb/brew
   brew install mongodb-community
   ```

2. Create a data directory:
   ```
   mkdir -p ~/data/db
   ```

3. Start MongoDB:
   ```
   mongod --dbpath ~/data/db
   ```

### Backend Setup

1. Navigate to the ContextCoach directory:
   ```
   cd ContextCoach
   ```

2. Build the application:
   ```
   ./mvnw clean install
   ```

3. Run the application:
   ```
   ./mvnw spring-boot:run
   ```

The backend will start on port 8080.

### Frontend Setup

1. Navigate to the feature-clarify-analyze directory:
   ```
   cd feature-clarify-analyze
   ```

2. Install dependencies:
   ```
   npm install
   ```

3. Run the development server:
   ```
   npm run dev
   ```

The frontend will start on port 3000.

## Running the Application

For convenience, you can use the provided script to run both the backend and frontend together:

```
./run-app.sh
```

This script will:
1. Check if MongoDB is running and start it if needed
2. Start the Spring Boot backend
3. Start the React frontend
4. Set up proper cleanup when you exit with Ctrl+C

Access the application at http://localhost:3000

## Features

- Upload feature descriptions as text or files
- Analyze feature complexity
- Generate implementation plans
- Calculate story points
- Provide developer-specific estimations

## Feature Analysis Workflow

The application follows a structured workflow to analyze feature requests and provide accurate complexity estimations:

```
┌────────────────────┐
│ Text File Input    │◄─── (New Feature Description)
└────────┬───────────┘
         │
         ▼
┌─────────────────────────────────────┐
│ Ambiguity Detection & Clarification │
│ - Use vector DB + LLM to check if   │
│   feature aligns with codebase      │
│ - Ask user clarifying questions     │
│ - Finalize clarified feature        │
└────────┬────────────────────────────┘
         ▼
┌──────────────────────────────┐
│  Preprocess + Embed Query    │
│  (Convert description to     │
│   relevant search prompts)   │
└────────┬─────────────────────┘
         ▼
┌──────────────────────────────┐
│   Query Local Vector DB      │◄─── (Chunked code repo already embedded)
└────────┬─────────────────────┘
         ▼
┌───────────────────────────────┐
│  Collect Relevant Code Chunks │
└────────┬──────────────────────┘
         ▼
┌─────────────────────────────────────────────┐
│  Feed Code + Feature + Developer Profile to │
│     LLM for Complexity Analysis             │
│  - Dev profile includes experience, role,   │
│    familiarity with repo, etc.              │
└────────┬────────────────────────────────────┘
         ▼
┌───────────────────────────────┐
│      Output Final Report:     │
│  - Complexity Level           │
│  - Story Points (dev-specific)│
│  - Subtasks                   │
│  - Affected Files             │
│  - Refactors & Risks          │
└───────────────────────────────┘
```

The workflow consists of the following steps:

1. **Input**: The process begins with a text file input containing the new feature description.

2. **Ambiguity Detection & Clarification**: 
   - Uses vector database and LLM to check if the feature aligns with the existing codebase
   - Asks clarifying questions to the user when needed
   - Finalizes the clarified feature description

3. **Query Preprocessing**: 
   - Converts the feature description into relevant search prompts
   - Embeds the query for vector database search

4. **Vector Database Query**: 
   - Searches the local vector database containing embedded chunks of the code repository

5. **Code Collection**: 
   - Gathers relevant code chunks based on the vector search results

6. **Complexity Analysis**: 
   - Feeds the collected code, feature description, and developer profile to an LLM
   - Developer profile includes experience, role, familiarity with the repository, etc.

7. **Final Report Generation**: 
   - Outputs a comprehensive report including:
     - Complexity level
     - Story points (developer-specific)
     - Subtasks breakdown
     - Affected files
     - Potential refactors and risks

## Integration

The frontend and backend are integrated through a REST API. The frontend uses axios to communicate with the backend, and the backend exposes endpoints for various operations related to feature analysis.

The integration is configured with a proxy in the Vite configuration to handle CORS issues during development.

## Version Control and GitHub

A comprehensive `.gitignore` file is included in the project root directory to ensure that only necessary files are committed to the repository. This file excludes:

- Build artifacts (target/, dist/, node_modules/)
- IDE-specific files (.idea/, .vscode/)
- Log files and directories
- Environment variables (.env)
- OS-specific files (.DS_Store, Thumbs.db)
- Temporary files

To push this project to GitHub, follow these steps:

1. **Initialize Git Repository**:
   ```bash
   # Navigate to the project root directory
   cd /path/to/ProgramManagerAI

   # Initialize Git repository
   git init
   ```

2. **Add Files to Git**:
   ```bash
   # Add all files to the repository (the .gitignore will ensure only appropriate files are added)
   git add .
   ```

3. **Commit Changes**:
   ```bash
   # Commit the files
   git commit -m "Initial commit"
   ```

4. **Create a GitHub Repository**:
   - Go to [GitHub](https://github.com/) and sign in
   - Click on the "+" icon in the top right corner and select "New repository"
   - Name your repository (e.g., "ProgramManagerAI")
   - Do not initialize the repository with a README, .gitignore, or license
   - Click "Create repository"

5. **Add GitHub as Remote and Push**:
   ```bash
   # Add the GitHub repository as a remote
   git remote add origin https://github.com/yourusername/ProgramManagerAI.git

   # Push to GitHub
   git push -u origin main
   ```
   
   If your default branch is named "master" instead of "main", use:
   ```bash
   git push -u origin master
   ```

### Troubleshooting GitHub Push Issues

If you encounter issues pushing to GitHub:

1. **Authentication Issues**: Ensure you have the correct credentials set up. GitHub now uses personal access tokens instead of passwords:
   ```bash
   # Configure Git to use credential helper
   git config --global credential.helper cache
   ```
   
   Or generate a personal access token on GitHub and use it as your password.

2. **Large Files**: GitHub has a file size limit of 100MB. If you have large files, consider using Git LFS or adding them to .gitignore.

3. **Git Configuration**: Ensure your Git user is configured:
   ```bash
   git config --global user.name "Your Name"
   git config --global user.email "your.email@example.com"
   ```

## Logging

### Frontend Logging
The frontend application includes a comprehensive logging system that:
- Logs all API requests and responses
- Captures errors and warnings
- Stores logs in localStorage for persistence
- Provides a user interface to view, filter, and download logs

To access the frontend logs, navigate to the "Application Logs" page in the sidebar.

### Backend Logging
The backend uses Logback for logging with the following features:
- Console logging with colored output for development
- File-based logging with rotation
- Separate log files for different log levels (all logs, errors only)
- API request/response logging with timing information
- Unique request IDs for tracing requests through the system

Log files are stored in the `logs` directory:
- `contextcoach.log`: All application logs
- `error.log`: Error logs only
- `api-requests.log`: API request/response logs

The logging configuration can be customized in `src/main/resources/logback-spring.xml`.
