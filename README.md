# 🐕 Dog Walks REST API

API REST developed with Java Spring Boot to manage dog walks. Authenticated users can create and administrate dog walks in a secure ay, with differentiated roles.

## 📋 Tabla de Contenidos

- [Description](#description)
- [Main Characteristics](#main-characteristics)
- [Used technologies](#used-technologies)
- [Project architecture](#project-architecture)
- [Modelo de Datos](#modelo-de-datos)
- [Requisitos Previos](#requisitos-previos)
- [Instalación](#instalación)
- [Configuración](#configuración)
- [Uso de la API](#uso-de-la-api)
- [Endpoints Disponibles](#endpoints-disponibles)
- [Seguridad y Autenticación](#seguridad-y-autenticación)
- [Testing](#testing)
- [Contribuir](#contribuir)

## 🎯 Descripción

Dog Walks API es un servicio backend que permite a los usuarios gestionar paseos de perros. La aplicación implementa autenticación JWT, roles de usuario (USER/ADMIN), y operaciones CRUD completas para usuarios y paseos.

## ✨ Características Principales

- 🔐 **Autenticación JWT**: Sistema seguro de tokens para autenticación stateless
- 👥 **Sistema de roles**: Diferenciación entre usuarios regulares y administradores
- 🐕 **Gestión de paseos**: CRUD completo para paseos con información detallada
- 👤 **Gestión de perfiles**: Los usuarios pueden actualizar sus datos personales
- 🛡️ **Seguridad avanzada**: Endpoints protegidos y validación de autorización
- 📊 **Endpoints públicos**: Acceso sin autenticación para consultar paseos
- 🧪 **Testing completo**: Suite de tests de integración con TestContainers
- ⚙️ **Configuración por variables**: Todas las configuraciones externalizadas

## 🛠️ Tecnologías Utilizadas

### Backend Framework
- **Java 21**: Versión LTS más reciente
- **Spring Boot 3.5.4**: Framework principal
- **Spring Security**: Autenticación y autorización
- **Spring Data JPA**: Acceso a datos con Hibernate
- **Spring Validation**: Validación de datos de entrada

### Base de Datos
- **MySQL 8.0**: Base de datos principal para todos los entornos
- **MySQL Connector/J**: Driver oficial de MySQL para Java

### Seguridad
- **JWT (JJWT 0.12.6)**: Tokens JSON Web seguros
- **BCrypt**: Hash seguro de contraseñas integrado en Spring Security

### Testing
- **JUnit 5**: Framework de testing
- **TestContainers**: Tests de integración con MySQL real
- **Spring Boot Test**: Utilidades de testing
- **MockMvc**: Testing de controladores

### Herramientas de Desarrollo
- **Lombok**: Generación automática de código boilerplate
- **Spring DevTools**: Recarga automática en desarrollo
- **Spring Dotenv**: Gestión de variables de entorno
- **Maven**: Gestión de dependencias

## 🏗️ Arquitectura del Proyecto

```
src/main/java/com/backend/dogwalks/
├── auth/
│   ├── controller/     # AuthController - registro y login
│   ├── dto/           # DTOs para autenticación
│   └── service/       # Lógica de autenticación
├── user/
│   ├── controller/    # CustomUserController, AdminController
│   ├── dto/          # DTOs para usuarios
│   ├── entity/       # CustomUser entity
│   ├── enums/        # Role enum
│   ├── repository/   # UserRepository
│   └── service/      # Servicios de usuario
├── walk/
│   ├── controller/   # WalkController
│   ├── dto/         # DTOs para paseos
│   ├── entity/      # Walk entity
│   ├── repository/  # WalkRepository
│   └── service/     # Servicios de paseos
├── security/
│   └── user/
│       ├── jwt/     # JwtUtil, JwtAuthFilter
│       └── service/ # CustomUserDetailsService
├── config/
│   └── security/    # SecurityConfig
└── exception/       # Manejo global de excepciones
```

## 📊 Modelo de Datos

### Entidad CustomUser
```java
@Entity
@Table(name = "users")
public class CustomUser {
    private Long id;
    private String username;        // Nombre de usuario único
    private String email;          // Email único
    private String password;       // Hash BCrypt
    private String userImgUrl;     // URL imagen perfil
    private Role role;             // USER o ADMIN
    private Boolean isActive;      // Estado activo/inactivo
    private List<Walk> walks;      // Paseos creados
}
```

### Entidad Walk
```java
@Entity
@Table(name = "walks")
public class Walk {
    private Long id;
    private String title;           // Título del paseo
    private LocalDateTime createdAt;// Fecha de creación automática
    private String location;        // Ubicación
    private Integer duration;       // Duración en minutos
    private String description;     // Descripción del paseo
    private String walkImgUrl;      // URL imagen del paseo
    private Boolean isActive;       // Estado activo/inactivo
    private CustomUser user;        // Usuario creador
}
```

## ⚙️ Requisitos Previos

- **Java 21** o superior
- **Maven 3.6+**
- **MySQL 8.0** o superior
- **IDE** recomendado: IntelliJ IDEA, Eclipse, o VS Code

## 🚀 Instalación

### 1. Clonar el repositorio
```bash
git clone https://github.com/J-uds/dog-walks.git
cd dog-walks
```

### 2. Configurar base de datos MySQL
```sql
CREATE DATABASE dogwalks;
CREATE USER 'dogwalks_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON dogwalks.* TO 'dogwalks_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Configurar variables de entorno
Crea un archivo `.env` en la raíz del proyecto:
```properties
# Base de datos
DB_URL=jdbc:mysql://localhost:3306/dogwalks
DB_USER=dogwalks_user
DB_PASSWORD=your_password

# JWT
JWT_SECRET=tu_clave_secreta_muy_larga_y_segura_de_al_menos_256_bits
JWT_EXPIRATION=86400000

# Admin inicial
ADMIN_EMAIL=admin@dogwalks.com
ADMIN_PASSWORD=AdminPassword123!

# Servidor
SERVER_PORT=8080
```

### 4. Compilar y ejecutar
```bash
mvn clean compile
mvn spring-boot:run
```

La aplicación estará disponible en `http://localhost:8080`

## 🔧 Configuración

### Variables de Entorno Requeridas

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `DB_URL` | URL de conexión MySQL | `jdbc:mysql://localhost:3306/dogwalks` |
| `DB_USER` | Usuario de base de datos | `dogwalks_user` |
| `DB_PASSWORD` | Contraseña de base de datos | `your_secure_password` |
| `JWT_SECRET` | Clave secreta para JWT | `mi_clave_super_secreta_256_bits` |
| `JWT_EXPIRATION` | Tiempo expiración JWT (ms) | `86400000` (24 horas) |
| `ADMIN_EMAIL` | Email del admin inicial | `admin@dogwalks.com` |
| `ADMIN_PASSWORD` | Contraseña del admin | `AdminPassword123!` |
| `SERVER_PORT` | Puerto del servidor | `8080` |

### Perfiles de aplicación
- **Desarrollo**: Usa las variables del archivo `.env`
- **Testing**: Configuración automática con TestContainers
- **Producción**: Variables de entorno del sistema

## 📚 Uso de la API

### Base URL
```
http://localhost:8080/api
```

### Headers requeridos
```http
Content-Type: application/json
Authorization: Bearer {jwt_token}  # Para endpoints protegidos
```

## 🛣️ Endpoints Disponibles

### 🔐 Autenticación (Públicos)

#### Registro de Usuario
```http
POST /api/register
Content-Type: application/json

{
  "username": "maria_garcia",
  "email": "maria@example.com", 
  "password": "SecurePass123!"
}
```

**Respuesta exitosa (201):**
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

**Respuesta exitosa (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "username": "maria_garcia",
  "email": "maria@example.com"
}
```

### 🐕 Gestión de Paseos

#### Consultar paseos (Público)
```http
GET /api/walks/public?page=0&size=10&sortBy=createdAt&sortDirection=DESC
```

#### Ver detalle de paseo (Público)
```http
GET /api/walks/public/{id}
```

#### Crear paseo (Requiere autenticación)
```http
POST /api/walks
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "title": "Paseo matutino Retiro",
  "location": "Parque del Retiro, Madrid",
  "duration": 60,
  "description": "Paseo tranquilo por el parque",
  "walkImgUrl": "retiro.jpg",
  "isActive": true
}
```

#### Actualizar paseo propio
```http
PUT /api/walks/{id}
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "title": "Paseo actualizado",
  "duration": 90
}
```

#### Eliminar paseo propio
```http
DELETE /api/walks/{id}
Authorization: Bearer {jwt_token}
```

### 👤 Gestión de Perfil de Usuario

#### Obtener mi perfil
```http
GET /api/users/profile
Authorization: Bearer {jwt_token}
```

#### Actualizar mi perfil
```http
PUT /api/users/profile
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "username": "nuevo_nombre",
  "userImgUrl": "nueva_imagen.jpg"
}
```

#### Actualizar mi email
```http
PUT /api/users/profile/email
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "newEmail": "nuevo@email.com",
  "currentPassword": "mi_password_actual"
}
```

#### Actualizar mi contraseña
```http
PUT /api/users/profile/password
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "currentPassword": "password_actual",
  "newPassword": "nueva_password_123!",
  "confirmPassword": "nueva_password_123!"
}
```

#### Desactivar mi cuenta
```http
DELETE /api/users/profile/deactivate
Authorization: Bearer {jwt_token}
```

### 👨‍💼 Administración (Solo ADMIN)

#### Listar todos los usuarios (paginado)
```http
GET /api/admin/users?page=0&size=10&sortBy=id&sortDir=ASC
Authorization: Bearer {admin_jwt_token}
```

#### Ver usuario específico
```http
GET /api/admin/users/{id}
Authorization: Bearer {admin_jwt_token}
```

#### Actualizar usuario
```http
PUT /api/admin/users/{id}
Authorization: Bearer {admin_jwt_token}
Content-Type: application/json

{
  "username": "nuevo_nombre",
  "email": "nuevo@email.com",
  "userImgUrl": "imagen.jpg",
  "role": "USER",
  "isActive": true
}
```

#### Eliminar usuario
```http
DELETE /api/admin/users/{id}
Authorization: Bearer {admin_jwt_token}
```

## 🔒 Seguridad y Autenticación

### Flujo de Seguridad

1. **Registro**: Usuario crea cuenta con email único
2. **Login**: Validación de credenciales y generación de JWT
3. **Autenticación**: Cada petición incluye token JWT en header
4. **Autorización**: Verificación de roles y permisos por endpoint

### Roles y Permisos

| Endpoint | Público | USER | ADMIN |
|----------|---------|------|-------|
| `POST /api/register` | ✅ | ✅ | ✅ |
| `POST /api/login` | ✅ | ✅ | ✅ |
| `GET /api/walks/public/**` | ✅ | ✅ | ✅ |
| `POST /api/walks` | ❌ | ✅ | ✅ |
| `PUT /api/walks/{id}` | ❌ | ✅ (solo propios) | ✅ |
| `DELETE /api/walks/{id}` | ❌ | ✅ (solo propios) | ✅ |
| `GET /api/users/profile` | ❌ | ✅ | ✅ |
| `PUT /api/users/profile/**` | ❌ | ✅ | ✅ |
| `GET /api/admin/**` | ❌ | ❌ | ✅ |

### Configuración JWT

- **Algoritmo**: HMAC SHA-256
- **Expiración**: Configurable (por defecto 24 horas)
- **Claims**: username, id, role, iat, exp
- **Validación**: Automática en cada petición protegida

## 🧪 Testing

El proyecto incluye tests de integración completos usando TestContainers con MySQL real.

### Ejecutar todos los tests
```bash
mvn test
```

### Tests incluidos

- **AuthControllerIntegrationTest**: Tests de registro, login y validaciones
- **AdminControllerIntegrationTest**: Tests de administración de usuarios
- **CustomUserControllerIntegrationTest**: Tests de gestión de perfil
- **WalkControllerIntegrationTest**: Tests completos de CRUD de paseos

### Características de Testing

- **TestContainers**: MySQL 8.0 en contenedor para tests reales
- **@Transactional**: Rollback automático entre tests
- **Datos aislados**: Cada test limpia y prepara sus datos
- **Validación completa**: Status codes, JSON responses, base de datos

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