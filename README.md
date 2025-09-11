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
├── auth/             #Authentication 
├── user/             # User management
├── walk/             # Walk management
├── security/         # JWT & UserDetailsService
├── config/           # Security Config
└── exception/        # Global exception handling
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
Create a `.env` file in the project root (this part shows example values, change them as needed):
```env
# Database Configuration
DB_URL=...
DB_USER=...
DB_PASSWORD=...
DB_ROOT_PASSWORD=...

# JWT Configuration
JWT_SECRET=...
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
```

#### Step 4: Verify the application
The application will be available at:

**API**: `http://localhost:8080/api`

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
DB_URL=...
DB_USER=...
DB_PASSWORD=...

# JWT
JWT_SECRET=...
JWT_EXPIRATION=...

# Initial admin
ADMIN_EMAIL=...
ADMIN_PASSWORD=...

# Server
SERVER_PORT=...
```

#### Step 4: Build and run
```bash
mvn clean compile
mvn spring-boot:run
```

The application will be available at `http://localhost:8080`

## 🔧 Configuration

- .env contains DB credentials, JWT secret, admin user

- Supports Docker, Development and Testing profiles

### Application Profiles
- **Development**: Uses `.env` file variables
- **Docker**: Optimized for container environment
- **Testing**: Automatic configuration with TestContainers

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

## 🚀 Deployment

### Production Deployment with Docker

#### Docker Compose recommended for Production

#### Health Monitoring

The application includes health check endpoints:

```bash
# Check application health
curl http://localhost:8080/actuator/health

# Docker health checks are automatic
docker-compose ps  # Shows health status
```

#### Database Backup with Docker
```bash
# Create backup
docker-compose exec dogwalks-db mysqldump -u dog -p dogwalks > backup.sql

# Restore backup
docker-compose exec -T dogwalks-db mysql -u dog -p dogwalks < backup.sql
```

## 🚀 Future Improvements

- [ ] Enhanced pagination with location filters
- [ ] Email notification system
- [ ] Image upload API with Docker volume mounts
- [ ] Walk rating system
- [ ] CI/CD pipeline with Docker

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