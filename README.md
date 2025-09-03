# 🐕 Dog Walks REST API

A REST API built with Java Spring Boot for managing dog walks. Authenticated users can create and manage dog walks securely with differentiated roles.

## 📋 Table of Contents

- [Description](#description)
- [Key Features](#key-features)
- [Technologies Used](#technologies-used)
- [Project Architecture](#project-architecture)
- [Data Model](#data-model)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [API Usage](#api-usage)
- [Available Endpoints](#available-endpoints)
- [Security and Authentication](#security-and-authentication)
- [Testing](#testing)
- [Contributing](#contributing)

## 🎯 Description

Dog Walks API is a backend service that allows users to manage dog walks. The application implements JWT authentication, user roles (USER/ADMIN), and complete CRUD operations for users and walks.

## ✨ Key Features

- 🔐 **JWT Authentication**: Secure token system for stateless authentication
- 👥 **Role System**: Differentiation between regular users and administrators
- 🐕 **Walk Management**: Complete CRUD for walks with detailed information
- 👤 **Profile Management**: Users can update their personal data
- 🛡️ **Advanced Security**: Protected endpoints and authorization validation
- 📊 **Public Endpoints**: Unauthenticated access to query walks
- 🧪 **Complete Testing**: Integration test suite with TestContainers
- ⚙️ **Variable Configuration**: All configurations externalized

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
- **JWT (JJWT 0.12.6)**: Secure JSON Web Tokens
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

## 🏗️ Project Architecture

```
src/main/java/com/backend/dogwalks/
├── auth/
│   ├── controller/     # AuthController - registration and login
│   ├── dto/           # DTOs for authentication
│   └── service/       # Authentication logic
├── user/
│   ├── controller/    # CustomUserController, AdminController
│   ├── dto/          # DTOs for users
│   ├── entity/       # CustomUser entity
│   ├── enums/        # Role enum
│   ├── repository/   # UserRepository
│   └── service/      # User services
├── walk/
│   ├── controller/   # WalkController
│   ├── dto/         # DTOs for walks
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

- **Java 21** or higher
- **Maven 3.6+**
- **MySQL 8.0** or higher
- **IDE** recommended: IntelliJ IDEA, Eclipse, or VS Code

## 🚀 Installation

### 1. Clone the repository
```bash
git clone https://github.com/J-uds/dog-walks.git
cd dog-walks
```

### 2. Setup MySQL database
```sql
CREATE DATABASE dogwalks;
CREATE USER 'dogwalks_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON dogwalks.* TO 'dogwalks_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Configure environment variables
Create a `.env` file in the project root:
```properties
# Database
DB_URL=jdbc:mysql://localhost:3306/dogwalks
DB_USER=dogwalks_user
DB_PASSWORD=your_password

# JWT
JWT_SECRET=your_very_long_and_secure_secret_key_at_least_256_bits
JWT_EXPIRATION=86400000

# Initial admin
ADMIN_EMAIL=admin@dogwalks.com
ADMIN_PASSWORD=AdminPassword123!

# Server
SERVER_PORT=8080
```

### 4. Compile and run
```bash
mvn clean compile
mvn spring-boot:run
```

The application will be available at `http://localhost:8080`

## 🔧 Configuration

### Required Environment Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `DB_URL` | MySQL connection URL | `jdbc:mysql://localhost:3306/dogwalks` |
| `DB_USER` | Database user | `dogwalks_user` |
| `DB_PASSWORD` | Database password | `your_secure_password` |
| `JWT_SECRET` | JWT secret key | `my_super_secret_256_bit_key` |
| `JWT_EXPIRATION` | JWT expiration time (ms) | `86400000` (24 hours) |
| `ADMIN_EMAIL` | Initial admin email | `admin@dogwalks.com` |
| `ADMIN_PASSWORD` | Admin password | `AdminPassword123!` |
| `SERVER_PORT` | Server port | `8080` |

### Application profiles
- **Development**: Uses variables from `.env` file
- **Testing**: Automatic configuration with TestContainers
- **Production**: System environment variables

## 📚 API Usage

### Base URL
```
http://localhost:8080/api
```

### Required headers
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
  "username": "maria_garcia",
  "email": "maria@example.com", 
  "password": "SecurePass123!"
}
```

**Successful response (201):**
```json
{
  "id": 1,
  "username": "maria_garcia",
  "email": "maria@example.com"
}
```

#### Login
```http
POST /api/login
Content-Type: application/json

{
  "email": "maria@example.com",
  "password": "SecurePass123!"
}
```

**Successful response (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "username": "maria_garcia",
  "email": "maria@example.com"
}
```

### 🐕 Walk Management

#### Query walks (Public)
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
  "title": "Morning Walk at Retiro",
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

### 👨‍💼 Administration (ADMIN only)

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
  "username": "new_name",
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

| Endpoint | Public | USER | ADMIN |
|----------|---------|------|-------|
| `POST /api/register` | ✅ | ✅ | ✅ |
| `POST /api/login` | ✅ | ✅ | ✅ |
| `GET /api/walks/public/**` | ✅ | ✅ | ✅ |
| `POST /api/walks` | ❌ | ✅ | ✅ |
| `PUT /api/walks/{id}` | ❌ | ✅ (own only) | ✅ |
| `DELETE /api/walks/{id}` | ❌ | ✅ (own only) | ✅ |
| `GET /api/users/profile` | ❌ | ✅ | ✅ |
| `PUT /api/users/profile/**` | ❌ | ✅ | ✅ |
| `GET /api/admin/**` | ❌ | ❌ | ✅ |

### JWT Configuration

- **Algorithm**: HMAC SHA-256
- **Expiration**: Configurable (default 24 hours)
- **Claims**: username, id, role, iat, exp
- **Validation**: Automatic on each protected request

## 🧪 Testing

The project includes complete integration tests using TestContainers with real MySQL.

### Run all tests
```bash
mvn test
```

### Included tests

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
``` codes, JSON responses, base de datos

### Ejecutar tests específicos
```bash
# Solo tests de autenticación
mvn test -Dtest="*Auth*"

# Solo tests de administración  
mvn test -Dtest="*Admin*"

# Solo tests de paseos
mvn test -Dtest="*Walk*"
```

## 🚀 Próximas Mejoras

- [ ] Paginación mejorada con filtros por ubicación
- [ ] Sistema de notificaciones por email
- [ ] API de subida de imágenes
- [ ] Geolocalización con coordenadas GPS
- [ ] Sistema de valoraciones de paseos
- [ ] Integración con servicios de mapas
- [ ] Cache con Redis para mejor rendimiento
- [ ] Métricas con Micrometer/Actuator

## 🤝 Contribuir

1. Fork del proyecto
2. Crear rama para feature: `git checkout -b feature/nueva-funcionalidad`
3. Commit de cambios: `git commit -m 'Agregar nueva funcionalidad'`
4. Push a la rama: `git push origin feature/nueva-funcionalidad`
5. Crear Pull Request

### Guías de Contribución

- Seguir las convenciones de código del proyecto
- Escribir tests para nuevas funcionalidades
- Mantener cobertura de tests alta
- Documentar cambios en la API
- Usar mensajes de commit descriptivos

## 📝 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE.md](LICENSE.md) para detalles.

## 👨‍💻 Autor

**J-uds** - [GitHub](https://github.com/J-uds)

## 📞 Soporte

- Crear un [Issue en GitHub](https://github.com/J-uds/dog-walks/issues)
- Email: [Contactar al desarrollador]

---

⭐ Si este proyecto te resultó útil, ¡dale una estrella en GitHub!

## 📚 Recursos Adicionales

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [JWT Introduction](https://jwt.io/introduction)
- [TestContainers Documentation](https://testcontainers.com)
- [MySQL 8.0 Reference Manual](https://dev.mysql.com/doc/refman/8.0/en/)