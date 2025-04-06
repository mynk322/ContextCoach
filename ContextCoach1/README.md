# ContextCoach

ContextCoach is an AI-powered requirement analysis tool that helps software development teams analyze, clarify, and estimate requirements. It uses RabbitHole's Claude models to detect ambiguities, estimate scope, and generate implementation plans for software requirements.

## Features

- **Requirement Analysis**: Upload requirements from various file formats (PDF, TXT, JSON, Excel) or enter them directly.
- **Ambiguity Detection**: Identify vague or unclear specifications in requirements.
- **Scope Estimation**: Get time and complexity estimates for implementing requirements.
- **Implementation Planning**: Generate detailed implementation plans with steps and technical approaches.
- **Jira Integration**: Create Jira tickets based on analyzed requirements.
- **Developer Profiles**: Manage developer profiles with skills and productivity factors.
- **Feature Complexity Analysis CLI**: Interactive command-line tool to clarify feature requests and analyze implementation complexity.

## Technology Stack

- **Backend**: Spring Boot, Java 17
- **Database**: MongoDB
- **AI Integration**: RabbitHole Claude models
- **Document Processing**: Apache PDFBox, Apache POI
- **Vector Database**: Python-based vector embeddings for code search
- **Build Tool**: Maven

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- MongoDB 4.4 or higher
- RabbitHole API key

### MongoDB Setup

Please refer to [MONGODB.md](MONGODB.md) for detailed MongoDB setup instructions.

### Installation

1. Clone the repository:
   ```
   git clone https://github.com/yourusername/context-coach.git
   cd context-coach
   ```

2. Set up environment variables:
   ```
   export RABBITHOLE_API_KEY=your-rabbithole-api-key
   ```

3. Ensure MongoDB is running:
   ```
   ./init-db.sh
   ```

4. Build the application:
   ```
   mvn clean install
   ```

5. Run the application:
   ```
   mvn spring-boot:run
   ```

6. Access the application at:
   ```
   http://localhost:8080/contextcoach
   ```

### Using the Feature Complexity Analysis CLI

The Feature Complexity Analysis CLI tool helps you interactively clarify feature requests and analyze their implementation complexity.

1. Make the script executable:
   ```
   chmod +x cli/feature-complexity-cli.sh
   ```

2. Run the CLI tool:
   ```
   ./cli/feature-complexity-cli.sh
   ```

3. You can also provide a feature request file:
   ```
   ./cli/feature-complexity-cli.sh cli/examples/sample-feature-request.txt
   ```

For more details, see the [CLI documentation](cli/README.md).

### Vector Database Setup

The Feature Complexity Analysis CLI can use a vector database to search for relevant code snippets when analyzing feature complexity. To set up the vector database:

1. Make the setup script executable:
   ```
   chmod +x vector-db-setup.sh
   ```

2. Run the setup script:
   ```
   ./vector-db-setup.sh
   ```

3. Set the environment variables to use the vector database:
   ```
   export USE_REAL_VECTOR_DB=true
   export VECTOR_DB_API_URL=http://localhost:5000
   ```

For more details, see the [Vector Database documentation](VECTOR_DB.md).

### Configuration

The application can be configured through the `application.properties` file:

- **RabbitHole API**: Set your API key and model
- **MongoDB**: Configure MongoDB connection settings
- **Jira Integration**: Set Jira API credentials if needed

## API Endpoints

### Requirements

- `POST /api/requirements/upload`: Upload a requirement file
- `POST /api/requirements/text`: Create a requirement from text
- `GET /api/requirements`: Get all requirements
- `GET /api/requirements/{id}`: Get a requirement by ID
- `POST /api/requirements/{id}/analyze`: Analyze a requirement for ambiguities
- `POST /api/requirements/{id}/estimate`: Estimate the scope of a requirement
- `POST /api/requirements/{id}/plan`: Generate an implementation plan for a requirement

### Jira Tickets

- `POST /api/jira/tickets`: Create a Jira ticket from a requirement
- `GET /api/jira/tickets`: Get all Jira tickets
- `GET /api/jira/tickets/{id}`: Get a Jira ticket by ID
- `GET /api/jira/tickets/requirement/{requirementId}`: Get all Jira tickets for a requirement
- `GET /api/jira/tickets/developer/{developerId}`: Get all Jira tickets assigned to a developer

### Developer Profiles

- `POST /api/developers`: Create a developer profile
- `GET /api/developers`: Get all developer profiles
- `GET /api/developers/{id}`: Get a developer profile by ID
- `GET /api/developers/search/name`: Search developer profiles by name
- `GET /api/developers/search/experience`: Search developer profiles by experience level
- `GET /api/developers/search/skill`: Search developer profiles by skill
- `PUT /api/developers/{id}`: Update a developer profile
- `DELETE /api/developers/{id}`: Delete a developer profile

## License

This project is licensed under the MIT License - see the LICENSE file for details.
