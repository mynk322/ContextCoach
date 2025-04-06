# MongoDB Setup for ContextCoach

This guide will help you set up MongoDB for the ContextCoach application.

## Prerequisites

- MongoDB 4.4+ installed on your system
- Java 17+ installed on your system
- Maven 3.6+ installed on your system

## Installation Options

### Option 1: Install MongoDB Locally

#### macOS (using Homebrew)

```bash
# Install MongoDB
brew tap mongodb/brew
brew install mongodb-community

# Start MongoDB service
brew services start mongodb-community
```

#### Linux (Ubuntu/Debian)

```bash
# Import MongoDB public GPG key
wget -qO - https://www.mongodb.org/static/pgp/server-5.0.asc | sudo apt-key add -

# Create list file for MongoDB
echo "deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu focal/mongodb-org/5.0 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-5.0.list

# Update packages and install MongoDB
sudo apt-get update
sudo apt-get install -y mongodb-org

# Start MongoDB service
sudo systemctl start mongod
sudo systemctl enable mongod
```

#### Windows

1. Download the MongoDB installer from [MongoDB Download Center](https://www.mongodb.com/try/download/community)
2. Run the installer and follow the instructions
3. MongoDB should be installed as a service and start automatically

### Option 2: Use Docker

```bash
# Pull the MongoDB image
docker pull mongo:latest

# Run MongoDB container
docker run -d -p 27017:27017 --name mongodb mongo:latest
```

## Verify Installation

Run the following command to check if MongoDB is running:

```bash
# Using MongoDB shell
mongosh
```

You should see the MongoDB shell prompt. Type `exit` to quit.

## Configure ContextCoach

The application is already configured to connect to MongoDB at `localhost:27017`. If your MongoDB instance is running on a different host or port, update the configuration in `src/main/resources/application.properties`:

```properties
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=contextcoachdb
```

## Running the Application

Once MongoDB is set up and running, you can start the application:

```bash
# Run the init script to check MongoDB connection
./init-db.sh

# Start the application
mvn spring-boot:run
```

## Troubleshooting

### Connection Issues

If the application cannot connect to MongoDB, check:

1. MongoDB service is running (`brew services list` on macOS, `systemctl status mongod` on Linux)
2. MongoDB is accessible on the configured host and port (`telnet localhost 27017`)
3. No firewall rules are blocking the connection

### Database Access

If you're using MongoDB with authentication enabled, update the configuration to include username and password:

```properties
spring.data.mongodb.username=your_username
spring.data.mongodb.password=your_password
spring.data.mongodb.authentication-database=admin
```

### Data Persistence

The application will automatically create the required collections and indexes in MongoDB. Sample data is initialized on first run. 