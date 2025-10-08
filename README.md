# Emoji Sphere Backend

A Spring Boot REST API backend for the Emoji Sphere application with MySQL database integration.

## Features

- User authentication and authorization with JWT
- RESTful API endpoints for posts, comments, likes
- Role-based access control (USER, MODERATOR, ADMIN)
- Category and tag management
- Post creation with emoji content support
- Like/unlike functionality
- Search and filtering capabilities
- CORS configuration for frontend integration

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security** (JWT Authentication)
- **Spring Data JPA** (Database Operations)
- **MySQL 8.0** (Database)
- **Maven** (Build Tool)
- **Lombok** (Code Generation)
- **ModelMapper** (DTO Mapping)

## Project Structure

```
src/main/java/com/emojisphere/
├── EmojiSphereBackendApplication.java  # Main Application Class
├── config/                             # Configuration Classes
│   ├── AuthEntryPointJwt.java
│   ├── AuthTokenFilter.java
│   ├── ModelMapperConfig.java
│   └── WebSecurityConfig.java
├── controller/                         # REST Controllers
│   ├── AuthController.java
│   ├── CategoryController.java
│   └── PostController.java
├── dto/                               # Data Transfer Objects
│   ├── CategoryResponse.java
│   ├── JwtResponse.java
│   ├── LoginRequest.java
│   ├── MessageResponse.java
│   ├── PostRequest.java
│   ├── PostResponse.java
│   ├── SignupRequest.java
│   ├── TagResponse.java
│   └── UserResponse.java
├── entity/                            # JPA Entities
│   ├── Category.java
│   ├── Comment.java
│   ├── ERole.java
│   ├── Like.java
│   ├── Post.java
│   ├── Role.java
│   ├── Tag.java
│   └── User.java
├── repository/                        # Data Access Layer
│   ├── CategoryRepository.java
│   ├── CommentRepository.java
│   ├── LikeRepository.java
│   ├── PostRepository.java
│   ├── RoleRepository.java
│   ├── TagRepository.java
│   └── UserRepository.java
└── service/                          # Business Logic Layer
    ├── CategoryService.java
    ├── PostService.java
    └── UserDetailsServiceImpl.java
```

## Database Setup

### Prerequisites
- MySQL 8.0 or higher installed
- MySQL server running on localhost:3306

### Database Configuration

1. Create a MySQL database named `emoji_sphere`:
```sql
CREATE DATABASE emoji_sphere;
```

2. Update database credentials in `src/main/resources/application.properties`:
```properties
spring.datasource.username=your_username
spring.datasource.password=your_password
```

3. The application will automatically create tables and populate initial data on startup.

## Getting Started

### 1. Clone and Navigate
```bash
cd D:\CodeBase\emoji-sphere-backend
```

### 2. Install Dependencies
```bash
mvn clean install
```

### 3. Run the Application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8081/api`

## API Endpoints

### Authentication
- `POST /api/auth/signup` - Register new user
- `POST /api/auth/signin` - User login

### Posts
- `GET /api/posts` - Get all public posts (paginated)
- `GET /api/posts/{id}` - Get specific post
- `POST /api/posts` - Create new post (authenticated)
- `PUT /api/posts/{id}` - Update post (authenticated, owner only)
- `DELETE /api/posts/{id}` - Delete post (authenticated, owner only)
- `GET /api/posts/user/{username}` - Get user's posts
- `GET /api/posts/search?keyword={keyword}` - Search posts
- `GET /api/posts/trending` - Get trending posts
- `POST /api/posts/{id}/like` - Toggle like on post (authenticated)

### Categories
- `GET /api/categories` - Get all active categories
- `GET /api/categories/{id}` - Get specific category

## Configuration

### JWT Configuration
```properties
app.jwt.secret=mySecretKey
app.jwt.expiration=86400000  # 24 hours
```

### CORS Configuration
```properties
app.cors.allowed-origins=http://localhost:3000,http://localhost:5173
```

## Default Data

The application comes with pre-populated data:
- **Roles**: USER, MODERATOR, ADMIN
- **Categories**: General, Humor, Art, Technology, Games, Food, Travel, Music, Sports, Education
- **Tags**: fun, tutorial, question, news, emoji, beginner, advanced, discussion, help, showcase

## Security

- JWT-based authentication
- Password encryption using BCrypt
- Role-based authorization
- CORS enabled for frontend integration
- Protection against common security vulnerabilities

## Development Profile

For development, you can use the development profile:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

This uses a separate development database configuration.

## Testing

Run tests with:
```bash
mvn test
```

## Integration with Frontend

This backend is designed to work with the React frontend located at `D:\CodeBase\emoji-sphere`. 

Key integration points:
- CORS configured for localhost:3000 and localhost:5173
- JWT tokens for authentication
- RESTful API design
- Consistent response formats

## Troubleshooting

### Common Issues

1. **Database Connection Error**
   - Ensure MySQL is running
   - Check database credentials in application.properties
   - Verify database exists

2. **JWT Token Issues**
   - Check JWT secret configuration
   - Ensure proper Authorization header format: `Bearer <token>`

3. **CORS Issues**
   - Verify allowed origins in application.properties
   - Check CORS configuration in WebSecurityConfig

## Production Deployment

For production deployment:

1. Update database configuration for production MySQL instance
2. Change JWT secret to a secure random value
3. Update CORS origins to production frontend URL
4. Set appropriate logging levels
5. Configure SSL/HTTPS
6. Set up proper environment variables for sensitive data

## Contributing

1. Follow the existing code structure and naming conventions
2. Add appropriate validation and error handling
3. Include proper logging for debugging
4. Update documentation for new features
5. Add tests for new functionality