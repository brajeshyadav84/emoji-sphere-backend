#!/bin/bash

# Emoji Sphere Authentication API Test Script
# Make sure the backend server is running on localhost:8080

BASE_URL="http://localhost:8080/auth"
MOBILE="+1234567890"
EMAIL="test@example.com"
PASSWORD="testPassword123"
NEW_PASSWORD="newPassword123"
OTP="123456"

echo "🚀 Starting Emoji Sphere Authentication API Tests"
echo "=================================================="

# Test 1: Validate Mobile Number
echo "📱 Test 1: Validate Mobile Number"
curl -X POST "$BASE_URL/validate-mobile" \
  -H "Content-Type: application/json" \
  -d "{\"mobile\": \"$MOBILE\"}"
echo -e "\n"

# Test 2: Send OTP
echo "📨 Test 2: Send OTP"
curl -X POST "$BASE_URL/send-otp" \
  -H "Content-Type: application/json" \
  -d "{\"mobile\": \"$MOBILE\"}"
echo -e "\n"

# Test 3: Verify OTP
echo "✅ Test 3: Verify OTP"
curl -X POST "$BASE_URL/verify-otp" \
  -H "Content-Type: application/json" \
  -d "{\"mobile\": \"$MOBILE\", \"otp\": \"$OTP\"}"
echo -e "\n"

# Test 4: Check Email Availability
echo "📧 Test 4: Check Email Availability"
curl -X POST "$BASE_URL/check-email" \
  -H "Content-Type: application/json" \
  -d "\"$EMAIL\""
echo -e "\n"

# Test 5: Sign Up
echo "👤 Test 5: User Registration (Sign Up)"
curl -X POST "$BASE_URL/signup" \
  -H "Content-Type: application/json" \
  -d "{
    \"fullName\": \"John Doe\",
    \"mobile\": \"$MOBILE\",
    \"email\": \"$EMAIL\",
    \"password\": \"$PASSWORD\",
    \"confirmPassword\": \"$PASSWORD\",
    \"age\": 25,
    \"country\": \"United States\",
    \"gender\": \"Male\"
  }"
echo -e "\n"

# Test 6: Sign In
echo "🔑 Test 6: User Login (Sign In)"
SIGNIN_RESPONSE=$(curl -X POST "$BASE_URL/signin" \
  -H "Content-Type: application/json" \
  -d "{\"mobile\": \"$MOBILE\", \"password\": \"$PASSWORD\"}" \
  -s)
echo "$SIGNIN_RESPONSE"

# Extract JWT token for future use
JWT_TOKEN=$(echo "$SIGNIN_RESPONSE" | grep -o '"token":"[^"]*' | cut -d'"' -f4)
echo "JWT Token: $JWT_TOKEN"
echo -e "\n"

# Test 7: Forgot Password
echo "🔒 Test 7: Forgot Password"
curl -X POST "$BASE_URL/forgot-password" \
  -H "Content-Type: application/json" \
  -d "{\"mobile\": \"$MOBILE\"}"
echo -e "\n"

# Test 8: Reset Password
echo "🔄 Test 8: Reset Password"
curl -X POST "$BASE_URL/reset-password" \
  -H "Content-Type: application/json" \
  -d "{
    \"mobile\": \"$MOBILE\",
    \"otp\": \"$OTP\",
    \"newPassword\": \"$NEW_PASSWORD\",
    \"confirmPassword\": \"$NEW_PASSWORD\"
  }"
echo -e "\n"

# Test 9: Sign In with New Password
echo "🔐 Test 9: Sign In with New Password"
curl -X POST "$BASE_URL/signin" \
  -H "Content-Type: application/json" \
  -d "{\"mobile\": \"$MOBILE\", \"password\": \"$NEW_PASSWORD\"}"
echo -e "\n"

echo "=================================================="
echo "✨ All tests completed!"
echo "💡 Note: Some tests may fail if:"
echo "   - The backend server is not running"
echo "   - Database is not set up correctly"
echo "   - OTP service is not configured"
echo "   - User already exists in the database"