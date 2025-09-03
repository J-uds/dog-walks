# 🐕 Dog Walks REST API

A REST API developed with Java Spring Boot for managing dog walks. Authenticated users can create and manage dog walks securely with differentiated roles.

## 📋 Table of Contents

- [Description](#description)
- [Key Features](#key-features)
- [Technologies Used](#technologies-used)
- [Project Architecture](#project-architecture)
- [Data Model](#data-model)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
    - [Option 1: With Docker (Recommended)](#option-1-with-docker-recommended)
    - [Option 2: Manual Installation](#option-2-manual-installation)
- [Configuration](#configuration)
- [API Usage](#api-usage)
- [Available Endpoints](#available-endpoints)
- [Security and Authentication](#security-and-authentication)
- [Testing](#testing)
- [Deployment](#deployment)
- [Contributing](#contributing)

## 🎯 Description

Dog Walks API is a backend service that allows users to manage dog walks. The application implements JWT authentication, user roles (USER/ADMIN), and complete CRUD operations for users and walks.

## ✨ Key Features

- 🔐 **JWT Authentication**: Secure token system for stateless authentication
- 👥 **Role System**: Differentiation between regular users and administrators
- 🐕 **Walk Management**: Complete CRUD for walks with detailed information
- 👤 **Profile Management**: Users can update their personal data
- 🛡️ **Advanced Security**: Protected endpoints and authorization validation
- 📊 **Public Endpoints**: Unauthenticated access to consult walks
- 🧪 **Complete Testing**: Integration test suite with TestContainers
- ⚙️ **Environment Configuration**: All configurations externalized
- 🐳 **Containerization**: Easy deployment with Docker and Docker Compose

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
- **TestContainers**: Integration tests with real MySQL
- **Spring Boot Test**: Testing utilities
- **MockMvc**: Controller testing

### Development Tools
- **Lombok**: Automatic boilerplate code generation
- **Spring DevTools**: Automatic reload in development
- **Spring Dotenv**: Environment variable management
- **Maven**: Dependency management

### Containerization
- **Docker**: Application containerization
- **Docker Compose**: Multi-container orchestration
- **Multi-stage Build**: Optimized Docker image creation

## 🏗️ Project Architecture

```
src/main/java/com/backend/dogwalks/
├── auth/
│   ├── controller/     # AuthController - registration and login
│   ├── dto/           # DTOs for authentication
│   └── service/       # Authentication logic
├── user/
│   ├── controller/    # CustomUserController, AdminController
│   ├── dto/          # User DTOs
│   ├── entity/       # CustomUser entity
│   ├── enums/        # Role enum
│   ├── repository/   # UserRepository
│   └── service/      # User services
├── walk/
│   ├── controller/   # WalkController
│   ├── dto/         # Walk DTOs
│   ├── entity/      # Walk entity
│   ├── repository/  # WalkRepository
│   └── service/     # Walk services
├── security/
│   └── user/
│       ├── jwt/     # JwtUtil, JwtAuthFilter
│       └── service/ # CustomUserDetailsService
├── config/
│   └── security/    # SecurityConfig
└── exception/       # Global exception handling
```

## 📊 Data Model

### CustomUser Entity
```java
@Entity
@Table(name = "users")
public class CustomUser {
    private Long id;
    private String username;        // Unique username
    private String email;          // Unique email
    private String password;       // BCrypt hash
    private String userImgUrl;     // Profile image URL
    private Role role;             // USER or ADMIN
    private Boolean isActive;      // Active/inactive status
    private List<Walk> walks;      // Created walks
}
```

### Walk Entity
```java
@Entity
@Table(name = "walks")
public class Walk {
    private Long id;
    private String title;           // Walk title
    private LocalDateTime createdAt;// Automatic creation date
    private String location;        // Location
    private Integer duration;       // Duration in minutes
    private String description;     // Walk description
    private String walkImgUrl;      // Walk image URL
    private Boolean isActive;       // Active/inactive status
    private CustomUser user;        // Creator user
}
```

## ⚙️ Prerequisites

### For Docker Installation (Recommended)
- **Docker 20.0+**
- **Docker Compose 2.0+**

### For Manual Installation
- **Java 21** or higher
- **Maven 3.6+**
- **MySQL 8.0** or higher
- **IDE** recommended: IntelliJ IDEA, Eclipse, or VS Code

## 🚀 Installation

### Option 1: With Docker (Recommended)

This is the easiest way to run the application. Docker will handle all the setup automatically.

#### Step 1: Clone the repository
```bash
git clone https://github.com/J-uds/dog-walks.git
cd dog-walks
```

#### Step 2: Set up environment variables
Create a `.env` file in the project root (this part shows example values, change them as needed or wanted):
```env
# Database Configuration
DB_USER=dog
DB_PASSWORD=dog
DB_ROOT_PASSWORD=rootdog

# JWT Configuration
JWT_SECRET=your_super_secret_jwt_key_at_least_256_bits_long_for_security
JWT_EXPIRATION=3600000

# Initial Admin User
ADMIN_EMAIL=admin@example.com
ADMIN_PASSWORD=AdminPassword123?
```

#### Step 3: Run with Docker Compose
```bash
# Build and start all services
docker-compose up -d

# Check if services are running
docker-compose ps

# View logs
docker-compose logs -f dogwalks-app
```

#### Step 4: Verify the application
The application will be available at:
- **API**: `http://localhost:8080/api`
- **Health Check**: `http://localhost:8080/actuator/health`
- **Database**: `localhost:3307` (externally accessible)

#### Docker Commands Reference
```bash
# Stop all services
docker-compose down

# Stop and remove volumes (deletes database data)
docker-compose down -v

# Rebuild and restart
docker-compose up --build -d

# View container logs
docker-compose logs dogwalks-app
docker-compose logs dogwalks-db

# Access application container shell
docker-compose exec dogwalks-app bash

# Access database container
docker-compose exec dogwalks-db mysql -u dog -p
```

### Option 2: Manual Installation

Choose this option if you prefer to install everything manually or for development purposes.

#### Step 1: Clone the repository
```bash
git clone https://github.com/J-uds/dog-walks.git
cd dog-walks
```

#### Step 2: Set up MySQL database
```sql
CREATE DATABASE dogwalks;
CREATE USER 'dogwalks_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON dogwalks.* TO 'dogwalks_user'@'localhost';
FLUSH PRIVILEGES;
```

#### Step 3: Configure environment variables
Create a `.env` file in the project root:
```properties
# Database
DB_URL=jdbc:mysql://localhost:3306/dogwalks
DB_USER=dogwalks_user
DB_PASSWORD=your_password

# JWT
JWT_SECRET=your_super_secret_jwt_key_at_least_256_bits_long_for_security
JWT_EXPIRATION=86400000

# Initial admin
ADMIN_EMAIL=admin@example.com
ADMIN_PASSWORD=AdminPassword123?

# Server
SERVER_PORT=8080
```

#### Step 4: Build and run
```bash
mvn clean compile
mvn spring-boot:run
```

The application will be available at `http://localhost:8080`

## 🔧 Configuration

### Docker Environment Variables

When using Docker Compose, configure these variables in your `.env` file:

| Variable | Description | Default | Example |
|----------|-------------|---------|---------|
| `DB_USER` | Database username | `dog` | `dog` |
| `DB_PASSWORD` | Database password | `dog` | `mypassword` |
| `DB_ROOT_PASSWORD` | MySQL root password | `rootdog` | `rootpassword` |
| `JWT_SECRET` | JWT secret key (256+ bits) | `change-me` | `my_super_secret_key` |
| `JWT_EXPIRATION` | Token expiration (ms) | `3600000` | `86400000` (24h) |
| `ADMIN_EMAIL` | Initial admin email | `admin@example.com` | `admin@dogwalks.com` |
| `ADMIN_PASSWORD` | Initial admin password | `admin123456.` | `AdminPass123!` |

### Manual Installation Environment Variables

For manual installation, use these variables in your `.env` file:

| Variable | Description | Example |
|----------|-------------|---------|
| `DB_URL` | MySQL connection URL | `jdbc:mysql://localhost:3306/dogwalks` |
| `DB_USER` | Database username | `dogwalks_user` |
| `DB_PASSWORD` | Database password | `your_secure_password` |
| `JWT_SECRET` | JWT secret key | `my_super_secret_256_bits_key` |
| `JWT_EXPIRATION` | Token expiration (ms) | `86400000` (24 hours) |
| `ADMIN_EMAIL` | Initial admin email | `admin@dogwalks.com` |
| `ADMIN_PASSWORD` | Initial admin password | `AdminPassword123!` |
| `SERVER_PORT` | Application port | `8080` |

### Application Profiles
- **Development**: Uses `.env` file variables
- **Docker**: Optimized for container environment
- **Testing**: Automatic configuration with TestContainers
- **Production**: System environment variables

### Docker Configuration Explained

The Docker setup includes three main configuration files:

#### 1. **docker-compose.yml** - Container Orchestration
This file defines how your application containers work together:

```yaml
# Two services: your app and the database
services:
  dogwalks-app:    # Your Spring Boot application
    build: .       # Build from Dockerfile in current directory
    ports: "8080:8080"  # Maps port 8080 inside container to port 8080 on your computer
    depends_on: dogwalks-db  # Wait for database to be ready
    
  dogwalks-db:     # MySQL database
    image: mysql:8.0  # Use official MySQL 8.0 image
    ports: "3307:3306"  # Maps MySQL port (avoid conflicts with local MySQL)
```

#### 2. **Dockerfile** - Application Container
This file defines how to build your application container:

```dockerfile
# Multi-stage build for efficiency
FROM maven:3.9.11-eclipse-temurin-21 AS build  # Build stage
FROM eclipse-temurin:21-jre                    # Runtime stage (smaller)
```

#### 3. **application-docker.yml** - Docker-specific Configuration
Special configuration when running in Docker:

```yaml
spring:
  datasource:
    url: jdbc:mysql://dogwalks-db:3306/dogwalks  # Uses Docker service name
```

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

### 🔐 Authentication (Public)

#### User Registration
```http
POST /api/register
Content-Type: application/json

{
  "username": "maria",
  "email": "maria@example.com", 
  "password": "SecurePass123?"
}
```

**Successful Response (201):**
```json
{
  "id": 1,
  "username": "maria",
  "email": "maria@example.com"
}
```

#### Login
```http
POST /api/login
Content-Type: application/json

{
  "email": "maria@example.com",
  "password": "SecurePass123?"
}
```

**Successful Response (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "username": "maria",
  "email": "maria@example.com"
}
```

### 🐕 Walk Management

#### Get walks (Public)
```http
GET /api/walks/public?page=0&size=10&sortBy=createdAt&sortDirection=DESC
```

#### View walk details (Public)
```http
GET /api/walks/public/{id}
```

#### Create walk (Requires authentication)
```http
POST /api/walks
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "title": "Morning walk at Retiro",
  "location": "Retiro Park, Madrid",
  "duration": 60,
  "description": "Peaceful walk through the park",
  "walkImgUrl": "retiro.jpg",
  "isActive": true
}
```

#### Update own walk
```http
PUT /api/walks/{id}
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "title": "Updated walk",
  "duration": 90
}
```

#### Delete own walk
```http
DELETE /api/walks/{id}
Authorization: Bearer {jwt_token}
```

### 👤 User Profile Management

#### Get my profile
```http
GET /api/users/profile
Authorization: Bearer {jwt_token}
```

#### Update my profile
```http
PUT /api/users/profile
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "username": "new_username",
  "userImgUrl": "new_image.jpg"
}
```

#### Update my email
```http
PUT /api/users/profile/email
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "newEmail": "new@email.com",
  "currentPassword": "my_current_password"
}
```

#### Update my password
```http
PUT /api/users/profile/password
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "currentPassword": "current_password",
  "newPassword": "new_password_123!",
  "confirmPassword": "new_password_123!"
}
```

#### Deactivate my account
```http
DELETE /api/users/profile/deactivate
Authorization: Bearer {jwt_token}
```

### 👨‍💼 Administration (ADMIN Only)

#### List all users (paginated)
```http
GET /api/admin/users?page=0&size=10&sortBy=id&sortDir=ASC
Authorization: Bearer {admin_jwt_token}
```

#### View specific user
```http
GET /api/admin/users/{id}
Authorization: Bearer {admin_jwt_token}
```

#### Update user
```http
PUT /api/admin/users/{id}
Authorization: Bearer {admin_jwt_token}
Content-Type: application/json

{
  "username": "new_username",
  "email": "new@email.com",
  "userImgUrl": "image.jpg",
  "role": "USER",
  "isActive": true
}
```

#### Delete user
```http
DELETE /api/admin/users/{id}
Authorization: Bearer {admin_jwt_token}
```

## 🔒 Security and Authentication

### Security Flow

1. **Registration**: User creates account with unique email
2. **Login**: Credential validation and JWT generation
3. **Authentication**: Each request includes JWT token in header
4. **Authorization**: Role and permission verification per endpoint

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

### JWT Configuration

- **Algorithm**: HMAC SHA-256
- **Expiration**: Configurable (default 1 hour in Docker, 24 hours manual)
- **Claims**: username, id, role, iat, exp
- **Validation**: Automatic on each protected request

## 🧪 Testing

The project includes complete integration tests using TestContainers with real MySQL.

### Run tests with Docker
```bash
# Using Docker Compose
docker-compose run --rm dogwalks-app mvn test

# Or build a test image
docker build -t dogwalks-test --target build .
docker run --rm dogwalks-test mvn test
```

### Run tests manually
```bash
mvn test
```

### Included Tests

- **AuthControllerIntegrationTest**: Registration, login and validation tests
- **AdminControllerIntegrationTest**: User administration tests
- **CustomUserControllerIntegrationTest**: Profile management tests
- **WalkControllerIntegrationTest**: Complete walk CRUD tests

### Testing Features

- **TestContainers**: MySQL 8.0 in container for real tests
- **@Transactional**: Automatic rollback between tests
- **Isolated data**: Each test cleans and prepares its data
- **Complete validation**: Status codes, JSON responses, database

### Run specific tests
```bash
# Authentication tests only
mvn test -Dtest="*Auth*"

# Administration tests only
mvn test -Dtest="*Admin*"

# Walk tests only
mvn test -Dtest="*Walk*"
```

## 🚀 Deployment

### Production Deployment with Docker

#### Using Docker Compose in Production
```bash
# Set production environment variables
export JWT_SECRET="your_super_secure_production_jwt_secret_key"
export DB_PASSWORD="secure_production_password"
export ADMIN_PASSWORD="secure_admin_password"

# Run in production mode
docker-compose -f docker-compose.yml up -d
```

#### Using Individual Docker Commands
```bash
# Create a network
docker network create dogwalks-network

# Run MySQL
docker run -d \
  --name dogwalks-db \
  --network dogwalks-network \
  -e MYSQL_DATABASE=dogwalks \
  -e MYSQL_USER=dog \
  -e MYSQL_PASSWORD=your_secure_password \
  -e MYSQL_ROOT_PASSWORD=your_root_password \
  -v dogwalks_data:/var/lib/mysql \
  mysql:8.0

# Build and run application
docker build -t dogwalks-app .
docker run -d \
  --name dogwalks-app \
  --network dogwalks-network \
  -p 8080:8080 \
  -e DB_URL="jdbc:mysql://dogwalks-db:3306/dogwalks" \
  -e DB_USER=dog \
  -e DB_PASSWORD=your_secure_password \
  -e JWT_SECRET="your_production_jwt_secret" \
  dogwalks-app
```

### Health Monitoring

The application includes health check endpoints:

```bash
# Check application health
curl http://localhost:8080/actuator/health

# Docker health checks are automatic
docker-compose ps  # Shows health status
```

### Backup and Data Management

#### Database Backup with Docker
```bash
# Create backup
docker-compose exec dogwalks-db mysqldump -u dog -p dogwalks > backup.sql

# Restore backup
docker-compose exec -T dogwalks-db mysql -u dog -p dogwalks < backup.sql
```

#### Persistent Data
- Database data is stored in Docker volume `mysql_data`
- Data persists even when containers are recreated
- Use `docker-compose down -v` only if you want to delete all data

## 🚀 Future Improvements

- [ ] Enhanced pagination with location filters
- [ ] Email notification system
- [ ] Image upload API with Docker volume mounts
- [ ] Geolocation with GPS coordinates
- [ ] Walk rating system
- [ ] Map service integration
- [ ] CI/CD pipeline with Docker

## 🤝 Contributing

1. Fork the project
2. Create feature branch: `git checkout -b feature/new-functionality`
3. Commit changes: `git commit -m 'Add new functionality'`
4. Push to branch: `git push origin feature/new-functionality`
5. Create Pull Request

### Contribution Guidelines

- Follow project code conventions
- Write tests for new functionalities
- Maintain high test coverage
- Document API changes
- Use descriptive commit messages
- Test with Docker before submitting PR

### Development with Docker
```bash
# Development mode with hot reload
docker-compose -f docker-compose.dev.yml up

# Run tests in development
docker-compose exec dogwalks-app mvn test
```

## 📝 License

## 📝 License

This project is licensed under the **Apache License 2.0** - see the [LICENSE](LICENSE.txt) file for details.


## 👨‍💻 Author

**J-uds** - [GitHub](https://github.com/J-uds)

## 📞 Support

- Create an [Issue on GitHub](https://github.com/J-uds/dog-walks/issues)
- Email: [Contact developer]

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