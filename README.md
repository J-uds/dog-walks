# 🐕 Dog Walks REST API

A REST API developed with Java Spring Boot for managing dog walks. Authenticated users can create and manage dog walks securely with differentiated roles.

## 📋 Table of Contents

- [Description](#-description)
- [Key Features](#-key-features)
- [Technologies Used](#-technologies-used)
- [Project Architecture](#-project-architecture)
- [Data Model](#-data-model)
- [Prerequisites](#-prerequisites)
- [Installation](#-installation)
    - [Option 1: With Docker (Recommended)](#-option-1-with-docker-recommended)
    - [Option 2: Manual Installation](#-option-2-manual-installation)
- [Configuration](#-configuration)
- [API Usage](#-api-usage)
- [Available Endpoints](#-available-endpoints)
- [Security and Authentication](#-security-and-authentication)
- [Testing](#-testing)
- [CI/CD and Deployment](#-cicd-and-deployment)
- [Monitoring](#-monitoring)
- [Future Improvements](#-future-improvements)
- [License](#-license)
- [Author](#-author)
- [Support](#-support)
- [Additional Resources](#-additional-resources)

## 🎯 Description

Dog Walks API is a backend service that allows users to manage dog walks. The application implements JWT authentication, user roles (USER/ADMIN), complete CRUD operations for users and walks, and includes automated testing and deployment workflows.

## ✨ Key Features

- 🔐 **JWT Authentication**: Secure token system for stateless authentication
- 👥 **Role System**: Differentiation between regular users and administrators
- 🐕 **Walk Management**: Complete CRUD for walks with detailed information
- 👤 **Profile Management**: Users can update their personal data
- 🛡️ **Advanced Security**: Protected endpoints and authorization validation
- 📊 **Public Endpoints**: Unauthenticated access to consult walks
- 🧪 **Complete Testing**: Integration test suite with TestContainers
- ⚙️ **Environment Configuration**: All configurations externalized
- 🐳 **Containerization**: Docker support with multi-stage builds and health checks
- 🚀 **Automated CI/CD**: GitHub Actions workflows for testing, building, and releasing
- 📈 **Health Monitoring**: Spring Actuator for application health and metrics

## 🛠️ Technologies Used

### Backend Framework
- **Java 21**: Latest LTS version
- **Spring Boot 3.5.4**: Main framework
- **Spring Security**: Authentication and authorization
- **Spring Data JPA**: Data access with Hibernate
- **Spring Validation**: Input data validation

### Database
- **MySQL 8.0**: Main database for all environments
- **MySQL Connector/J**: Official MySQL driver for Java

### Security
- **JWT (JJWT 0.12.6)**: Secure JSON Web tokens
- **BCrypt**: Secure password hashing integrated in Spring Security

### Testing
- **JUnit 5**: Testing framework
- **TestContainers**: Integration tests with real MySQL containers
- **Spring Boot Test**: Testing utilities
- **MockMvc**: Controller testing

### Development Tools
- **Lombok**: Automatic boilerplate code generation
- **Spring DevTools**: Automatic reload in development
- **Spring Dotenv**: Environment variable management
- **Maven**: Dependency management

### DevOps & Containerization
- **Docker**: Application containerization with multi-stage builds
- **Docker Compose**: Multi-container orchestration for development and production
- **GitHub Actions**: CI/CD pipeline automation
- **Docker Hub**: Container image registry

## 🏗️ Project Architecture

```
src/main/java/com/backend/dogwalks/
├── auth/             #Authentication 
├── user/             # User management
├── walk/             # Walk management
├── security/         # JWT & UserDetailsService
├── config/           # Security Config
└── exception/        # Global exception handling
```

### CI/CD Architecture
```
GitHub Repository
├── .github/workflows/
│   ├── test.yml      # PR validation (TestContainers)
│   ├── build.yml     # Main branch builds (Docker Hub)
│   └── release.yml   # Tagged releases (tests + Docker Hub)
├── docker-compose.yml        # Production deployment
└── docker-compose-test.yml   # Testing environment
```

## 📊 Data Model

### CustomUser Entity

- id, username, email, password, userImgUrl, role, isActive, walks.

### Walk Entity
- id, title, createdAt, location, duration, description, walkImgUrl, isActive, user.

## ⚙️ Prerequisites

### For Docker Installation (Recommended)
- **Docker 20.0+**
- **Docker Compose 2.0+**
- **Git** for cloning the repository

### For Manual Installation
- **Java 21** or higher
- **Maven 3.9+**
- **MySQL 8.0** or higher
- **IDE** recommended: IntelliJ IDEA, Eclipse, or VS Code

### For Development
- **Docker** (for TestContainers)
- **GitHub account** (for CI/CD workflows)
- **Docker Hub account** (for image publishing)

## 🚀 Installation

### Option 1: With Docker (Recommended)

This is the easiest way to run the application. Docker will handle all the setup automatically.

#### Step 1: Clone the repository
```bash
git clone https://github.com/J-uds/dog-walks.git
cd dog-walks
```

#### Step 2: Set up environment variables
Create a `.env` file in the project root (this part shows example values, change them as needed):
```env
# Database Configuration
DB_URL_LOCAL=...
DB_URL=...
DB_USER=...
DB_PASSWORD=...
DB_ROOT_PASSWORD=...

# JWT Configuration
JWT_SECRET=... # Must be 256+ bits
JWT_EXPIRATION=...

# Initial Admin User
ADMIN_EMAIL=...
ADMIN_PASSWORD=...
```

#### Step 3: Run with Docker Compose
```bash
# Build and start all services
docker-compose up -d

# Check if services are running
docker-compose ps

# View logs
docker-compose logs -f dogwalks-app

# View database logs
docker-compose logs -f dogwalks-db
```

#### Step 4: Verify the application
The application will be available at:

**API**: `http://localhost:8080/api`
- **Health Check**: `http://localhost:8080/actuator/health`
- **Database**: `localhost:3307` (MySQL)

### Option 2: Manual Installation

Choose this option if you prefer to install everything manually or for development purposes.

#### Step 1: Clone the repository
```bash
git clone https://github.com/J-uds/dog-walks.git
cd dog-walks
```

#### Step 2: Set up MySQL database
```sql
CREATE DATABASE databasename;
CREATE USER 'databasename_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON databasename.* TO 'databasename_user'@'localhost';
FLUSH PRIVILEGES;
```

#### Step 3: Configure environment variables
Create a `.env` file in the project root:
```properties
# Database
DB_URL_LOCAL=...
DB_URL=...
DB_USER=...
DB_PASSWORD=...

# JWT
JWT_SECRET=... # Must be 256+ bits
JWT_EXPIRATION=...

# Initial admin
ADMIN_EMAIL=...
ADMIN_PASSWORD=...

# Server
SERVER_PORT=...
```

#### Step 4: Build and run
```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Start the application
mvn spring-boot:run
```

The application will be available at `http://localhost:8080`

## 🔧 Configuration

- .env contains DB credentials, JWT secret, admin user

- Supports Docker, Development and Testing profiles

### Application Profiles
- **Development**: Uses `.env` file variables
- **Docker**: Optimized for container environment
- **Test**: Automatic configuration with TestContainers for integration testing

### Docker Configuration
- **Multi-stage Build**: Optimized for production with minimal image size
- **Health Checks**: Automatic health monitoring for both app and database
- **Network Isolation**: Dedicated Docker network for security
- **Volume Management**: Proper data persistence and caching

## 📚 API Usage

### Base URL
```
http://localhost:8080/api
```

### Required Headers
```http
Content-Type: application/json
Authorization: Bearer {jwt_token}  # For protected endpoints
```

## 🛣️ Available Endpoints

### 🔐 Public Endpoints

- Registration, login, list/view walks

### USER endpoints

- Create/update/delete own walks, profile management

### ADMIN endpoints

- User management, role updates

## 🔒 Security and Authentication

### Security Flow

1. **Registration**: User creates account with unique email and strong password
2. **Authentication**: Credential validation against BCrypt hashed password
3. **Token Generation**: JWT token created with user details and expiration
4. **Authorization**: Each request validates JWT token and user permissions
5. **Role Verification**: Endpoint access based on user role and resource ownership

### Roles and Permissions

| Endpoint | Public | USER         | ADMIN |
|----------|---------|--------------|-------|
| `POST /api/register` | ✅ | ✅            | ✅ |
| `POST /api/login` | ✅ | ✅            | ✅ |
| `GET /api/walks/public/**` | ✅ | ✅            | ✅ |
| `POST /api/walks` | ❌ | ✅            | ✅ |
| `PUT /api/walks/{id}` | ❌ | ✅ (own only) | ✅ |
| `DELETE /api/walks/{id}` | ❌ | ✅ (own only) | ✅ |
| `GET /api/users/profile` | ❌ | ✅ (own only) | ✅ |
| `PUT /api/users/profile` | ❌ | ✅ (own only) | ✅ |
| `GET /api/admin/**` | ❌ | ❌            | ✅ |

## 🧪 Testing

The project includes complete integration tests using TestContainers with real MySQL.

### Running Tests

#### Run test with Docker (CI/CD approach)
```bash
# Run tests using Docker Compose (recommended)
docker-compose -f docker-compose-test.yml up --abort-on-container-exit

# Clean up after tests
docker-compose -f docker-compose-test.yml down -v
```

### Run tests manually
```bash
mvn test
```

## 🚀 CI/CD and Deployment

### GitHub Actions Workflows

The project includes three automated workflows:

#### 1. Test Workflow (`.github/workflows/test.yml`)
- **Trigger**: Pull requests to main branch
- **Purpose**: Validate code changes before merge
- **Steps**:
  1. Checkout code
  2. Set up Docker Buildx
  3. Run TestContainers integration tests
  4. Clean up resources

#### 2. Build Workflow (`.github/workflows/build.yml`)
- **Trigger**: Pushes to main branch
- **Purpose**: Build and push Docker images for development
- **Steps**:
  1. Checkout code
  2. Set up Docker Buildx
  3. Login to Docker Hub
  4. Extract metadata (branch, SHA tags)
  5. Build and push Docker image with caching

#### 3. Release Workflow (`.github/workflows/release.yml`)
- **Trigger**: Git tags starting with "v" (e.g., v1.0.0)
- **Purpose**: Full testing and production image release
- **Steps**:
  1. Checkout code
  2. Set up Docker Buildx
  3. Run comprehensive tests with TestContainers
  4. Login to Docker Hub
  5. Build and push production image with "latest" tag
  6. Clean up test resources

### Docker Configuration

#### Multi-stage Dockerfile
```dockerfile
# Build stage (not shown in documents but implied)
FROM maven:3.9.11-eclipse-temurin-21 AS build
# ... build steps

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
# ... optimized runtime image
```

#### Production Deployment
```bash
# Deploy to production
git tag v1.0.0
git push origin v1.0.0  # Triggers release workflow

# Monitor deployment
docker-compose ps
docker-compose logs -f dogwalks-app
```

### Secrets Configuration

Required GitHub Secrets:
```yaml
DOCKER_USERNAME: your_dockerhub_username
DOCKER_PASSWORD: your_dockerhub_token
```

### Container Registry
- **Registry**: Docker Hub
- **Image**: `{username}/dogwalks`
- **Tags**:
  - Branch names (development)
  - Git SHA (traceability)
  - Version tags (releases)
  - `latest` (stable release)

## 📈 Monitoring

### Monitoring Commands
```bash
# Check service status
docker-compose ps

# Monitor logs in real-time
docker-compose logs -f dogwalks-app

# Check application health
curl http://localhost:8080/actuator/health

# View detailed service information
docker-compose exec dogwalks-app curl localhost:8080/actuator/info
```

### Database Backup and Maintenance
```bash
# Create database backup
docker-compose exec dogwalks-db mysqldump -u dog -pdog dogwalks > backup.sql

# Restore from backup
docker-compose exec -T dogwalks-db mysql -u dog -pdog dogwalks < backup.sql

# Monitor database health
docker-compose exec dogwalks-db mysqladmin status -u dog -pdog
```

## 🚀 Future Improvements

- [ ] Enhanced pagination with location filters
- [ ] Email notification system
- [ ] Walk rating system

## 📝 License

This project is licensed under the **Apache License 2.0** - see the [LICENSE](LICENSE.txt) file for details.

## 👨‍💻 Author

**J-uds** - [GitHub](https://github.com/J-uds)

## 📞 Support

- Create an [Issue on GitHub](https://github.com/J-uds/dog-walks/issues)

---

⭐ If this project was useful to you, give it a star on GitHub!

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [JWT Introduction](https://jwt.io/introduction)
- [TestContainers Documentation](https://testcontainers.com)
- [MySQL 8.0 Reference Manual](https://dev.mysql.com/doc/refman/8.0/en/)
- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Guide](https://docs.docker.com/compose/)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)