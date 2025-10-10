#!/bin/bash

echo "Testing User Profile APIs..."

BASE_URL="http://localhost:8080"
read -p "Enter mobile number for testing: " MOBILE
read -s -p "Enter password: " PASSWORD
echo

echo
echo "=========================================="
echo "1. Testing Login to get JWT token..."
echo "=========================================="

LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/signin" \
  -H "Content-Type: application/json" \
  -d "{\"mobile\":\"$MOBILE\",\"password\":\"$PASSWORD\"}")

echo "Login Response: $LOGIN_RESPONSE"

# Extract JWT token using jq (if available) or manual input
if command -v jq &> /dev/null; then
    JWT_TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.token')
    echo "JWT Token extracted: ${JWT_TOKEN:0:20}..."
else
    echo "Please copy the JWT token from the above response and paste it below:"
    read -p "JWT Token: " JWT_TOKEN
fi

echo
echo "=========================================="
echo "2. Testing Get User Profile..."
echo "=========================================="

curl -X GET "$BASE_URL/api/user/profile" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -w "\nStatus Code: %{http_code}\n"

echo
echo "=========================================="
echo "3. Testing Update User Profile (with school name)..."
echo "=========================================="

curl -X PUT "$BASE_URL/api/user/profile" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"schoolName":"Test University","country":"Test Country"}' \
  -w "\nStatus Code: %{http_code}\n"

echo
echo "=========================================="
echo "4. Testing Get User Profile After Update..."
echo "=========================================="

curl -X GET "$BASE_URL/api/user/profile" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -w "\nStatus Code: %{http_code}\n"

echo
echo "=========================================="
echo "5. Testing Signup with School Name..."
echo "=========================================="

echo "Enter details for new user signup test:"
read -p "New Mobile Number: " NEW_MOBILE
read -p "Full Name: " NEW_NAME
read -p "Email: " NEW_EMAIL
read -s -p "Password: " NEW_PASSWORD
echo
read -p "School Name: " SCHOOL_NAME

echo "First, sending OTP..."
curl -X POST "$BASE_URL/auth/send-otp" \
  -H "Content-Type: application/json" \
  -d "{\"mobile\":\"$NEW_MOBILE\"}" \
  -w "\nStatus Code: %{http_code}\n"

echo
read -p "Enter the OTP you received: " OTP

echo "Verifying OTP..."
curl -X POST "$BASE_URL/auth/verify-otp" \
  -H "Content-Type: application/json" \
  -d "{\"mobile\":\"$NEW_MOBILE\",\"otp\":\"$OTP\"}" \
  -w "\nStatus Code: %{http_code}\n"

echo
echo "Now testing signup with school name..."
curl -X POST "$BASE_URL/auth/signup" \
  -H "Content-Type: application/json" \
  -d "{\"fullName\":\"$NEW_NAME\",\"mobile\":\"$NEW_MOBILE\",\"email\":\"$NEW_EMAIL\",\"password\":\"$NEW_PASSWORD\",\"confirmPassword\":\"$NEW_PASSWORD\",\"age\":25,\"country\":\"Test Country\",\"gender\":\"Male\",\"schoolName\":\"$SCHOOL_NAME\"}" \
  -w "\nStatus Code: %{http_code}\n"

echo
echo "=========================================="
echo "Testing completed!"
echo "=========================================="