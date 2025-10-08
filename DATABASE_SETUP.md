# Database Setup for Emoji Sphere Backend

## MySQL Installation and Setup

### 1. Install MySQL 8.0
Download and install MySQL from [https://dev.mysql.com/downloads/mysql/](https://dev.mysql.com/downloads/mysql/)

### 2. Start MySQL Service
```bash
# Windows
net start mysql

# Or use MySQL Workbench / Services.msc
```

### 3. Connect to MySQL
```bash
mysql -u root -p
```

### 4. Create Database and User
```sql
-- Create database
CREATE DATABASE emoji_sphere;

-- Create dedicated user (optional, for security)
CREATE USER 'emoji_user'@'localhost' IDENTIFIED BY 'your_secure_password';
GRANT ALL PRIVILEGES ON emoji_sphere.* TO 'emoji_user'@'localhost';
FLUSH PRIVILEGES;

-- Use the database
USE emoji_sphere;
```

## Application Configuration

Update your `application.properties` with the correct database settings:

```properties
# For root user (default)
spring.datasource.url=jdbc:mysql://localhost:3306/emoji_sphere?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=your_root_password

# Or for dedicated user
spring.datasource.url=jdbc:mysql://localhost:3306/emoji_sphere?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=emoji_user
spring.datasource.password=your_secure_password
```

## Database Schema

The application will automatically create the following tables:

### Core Tables
- `users` - User accounts and profiles
- `roles` - User roles (USER, MODERATOR, ADMIN)
- `user_roles` - Many-to-many relationship between users and roles
- `posts` - User posts and content
- `categories` - Post categories
- `tags` - Post tags
- `post_tags` - Many-to-many relationship between posts and tags
- `comments` - Comments on posts
- `likes` - Likes on posts and comments

### Initial Data

The application will populate the following default data:

#### Roles
- Games 🎮
- Food 🍕
- Travel ✈️
- Music 🎵
- Sports ⚽
- Education 📚

#### Tags
- fun, tutorial, question, news, emoji
- beginner, advanced, discussion, help, showcase

## Verification Steps

1. **Check if database exists:**
```sql
SHOW DATABASES;
```

2. **Check if tables are created:**
```sql
USE emoji_sphere;
SHOW TABLES;
```

3. **Verify initial data:**
```sql
SELECT * FROM roles;
SELECT * FROM categories;
SELECT * FROM tags;
```

## Troubleshooting

### Connection Issues
- Ensure MySQL service is running
- Check firewall settings
- Verify port 3306 is available
- Test connection with MySQL Workbench

### Authentication Issues
- Check username/password in application.properties
- Ensure user has proper privileges
- Try connecting manually with mysql command line

### Encoding Issues
- Ensure MySQL is configured for UTF-8
- Check charset settings in application.properties

## Production Considerations

For production deployment:

1. **Use environment variables for sensitive data:**
```bash
export DB_PASSWORD=your_secure_password
```

2. **Update application.properties:**
```properties
spring.datasource.password=${DB_PASSWORD}
```

3. **Consider using connection pooling:**
```properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
```

4. **Set up proper backup strategy**
5. **Configure SSL for database connections**
6. **Monitor database performance and logs**