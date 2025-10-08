# Authentication API Documentation

## Overview
This document provides comprehensive documentation for all authentication-related APIs in the Emoji Sphere application.

## Base URL
```
http://localhost:8080/auth
```

## Endpoints

### 1. Send OTP
**Endpoint:** `POST /auth/send-otp`

**Description:** Sends an OTP to the provided mobile number for verification.

**Request Body:**
```json
{
  "mobile": "+1234567890"
}
```

**Response:**
```json
{
  "message": "OTP sent successfully to +1234567890"
}
```

**Error Response:**
```json
{
  "message": "Error: Failed to send OTP. [error details]"
}
```

---

### 2. Verify OTP
**Endpoint:** `POST /auth/verify-otp`

**Description:** Verifies the OTP sent to the mobile number.

**Request Body:**
```json
{
  "mobile": "+1234567890",
  "otp": "123456"
}
```

**Success Response:**
```json
{
  "success": true,
  "message": "OTP verified successfully"
}
```

**Error Response:**
```json
{
  "success": false,
  "message": "Invalid or expired OTP"
}
```

---

### 3. Validate Mobile Number
**Endpoint:** `POST /auth/validate-mobile`

**Description:** Checks if a mobile number is available for registration.

**Request Body:**
```json
{
  "mobile": "+1234567890"
}
```

**Success Response:**
```json
{
  "success": true,
  "message": "Mobile number is available"
}
```

**Error Response:**
```json
{
  "success": false,
  "message": "Mobile number is already registered!"
}
```

---

### 4. Check Email Availability
**Endpoint:** `POST /auth/check-email`

**Description:** Checks if an email address is available for registration.

**Request Body:**
```
"user@example.com"
```

**Success Response:**
```json
{
  "success": true,
  "message": "Email is available"
}
```

**Error Response:**
```json
{
  "success": false,
  "message": "Email is already in use!"
}
```

---

### 5. User Registration (Sign Up)
**Endpoint:** `POST /auth/signup`

**Description:** Registers a new user after OTP verification.

**Request Body:**
```json
{
  "fullName": "John Doe",
  "mobile": "+1234567890",
  "email": "john.doe@example.com",
  "password": "securePassword123",
  "confirmPassword": "securePassword123",
  "age": 25,
  "country": "United States",
  "gender": "Male"
}
```

**Success Response:**
```json
{
  "message": "User registered successfully!"
}
```

**Error Responses:**
```json
{
  "message": "Error: Password and confirm password do not match!"
}
```

```json
{
  "message": "Error: Mobile number is already registered!"
}
```

```json
{
  "message": "Error: Email is already in use!"
}
```

```json
{
  "message": "Error: Please verify your mobile number with OTP first!"
}
```

---

### 6. User Login (Sign In)
**Endpoint:** `POST /auth/signin`

**Description:** Authenticates a user and returns a JWT token.

**Request Body:**
```json
{
  "mobile": "+1234567890",
  "password": "securePassword123"
}
```

**Success Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "id": "+1234567890",
  "fullName": "John Doe",
  "mobile": "+1234567890",
  "email": "john.doe@example.com",
  "role": "USER",
  "roles": ["USER"]
}
```

**Error Response:**
```json
{
  "message": "Error: Invalid credentials"
}
```

---

### 7. Forgot Password
**Endpoint:** `POST /auth/forgot-password`

**Description:** Sends an OTP to the registered mobile number for password reset.

**Request Body:**
```json
{
  "mobile": "+1234567890"
}
```

**Success Response:**
```json
{
  "message": "Password reset OTP sent successfully to +1234567890"
}
```

**Error Responses:**
```json
{
  "message": "Error: No account found with this mobile number!"
}
```

```json
{
  "message": "Error: Failed to send password reset OTP. [error details]"
}
```

---

### 8. Reset Password
**Endpoint:** `POST /auth/reset-password`

**Description:** Resets the user's password after OTP verification.

**Request Body:**
```json
{
  "mobile": "+1234567890",
  "otp": "123456",
  "newPassword": "newSecurePassword123",
  "confirmPassword": "newSecurePassword123"
}
```

**Success Response:**
```json
{
  "message": "Password reset successfully!"
}
```

**Error Responses:**
```json
{
  "message": "Error: Invalid or expired OTP"
}
```

```json
{
  "message": "Error: Password and confirm password do not match!"
}
```

---

## Authentication Flow

### Registration Flow
1. **Validate Mobile** → `POST /auth/validate-mobile`
2. **Send OTP** → `POST /auth/send-otp`
3. **Verify OTP** → `POST /auth/verify-otp`
4. **Register User** → `POST /auth/signup`

### Login Flow
1. **Sign In** → `POST /auth/signin`
2. Use the returned JWT token in the `Authorization` header for protected endpoints

### Password Reset Flow
1. **Forgot Password** → `POST /auth/forgot-password`
2. **Verify OTP** → `POST /auth/verify-otp`
3. **Reset Password** → `POST /auth/reset-password`

## Error Codes

| Status Code | Description |
|-------------|-------------|
| 200 | Success |
| 400 | Bad Request (validation errors, business logic errors) |
| 401 | Unauthorized |
| 403 | Forbidden |
| 404 | Not Found |
| 500 | Internal Server Error |

## Security Notes

1. **JWT Token:** Include the JWT token in the Authorization header as `Bearer <token>` for protected endpoints.
2. **OTP Expiry:** OTPs expire after 5 minutes.
3. **Password Requirements:** Minimum 6 characters, maximum 40 characters.
4. **Mobile Number Format:** Should include country code (e.g., +1234567890).

## Field Validations

### SignupRequest
- `fullName`: Required, 2-100 characters
- `mobile`: Required, max 20 characters, unique
- `email`: Optional, valid email format, unique if provided
- `password`: Required, 6-40 characters
- `confirmPassword`: Required, must match password
- `age`: Optional, integer
- `country`: Optional, max 100 characters
- `gender`: Required, max 10 characters

### LoginRequest
- `mobile`: Required
- `password`: Required

### OtpRequest
- `mobile`: Required

### OtpVerifyRequest
- `mobile`: Required
- `otp`: Required

### ResetPasswordRequest
- `mobile`: Required
- `otp`: Required
- `newPassword`: Required, 6-40 characters
- `confirmPassword`: Required, must match newPassword

## Database Schema

### User Table (tbl_users)
```sql
CREATE TABLE tbl_users (
    mobile_number VARCHAR(20) PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    age INTEGER,
    gender VARCHAR(10),
    country VARCHAR(100),
    email VARCHAR(50) UNIQUE,
    password_hash VARCHAR(120) NOT NULL,
    is_verified BOOLEAN DEFAULT FALSE,
    role VARCHAR(20) DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### OTP Verification Table
```sql
CREATE TABLE otp_verifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    mobile VARCHAR(20) NOT NULL,
    otp VARCHAR(6) NOT NULL,
    verified BOOLEAN DEFAULT FALSE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```