@echo off
REM Emoji Sphere Authentication API Test Script for Windows
REM Make sure the backend server is running on localhost:8080

set BASE_URL=http://localhost:8080/auth
set MOBILE=+1234567890
set EMAIL=test@example.com
set PASSWORD=testPassword123
set NEW_PASSWORD=newPassword123
set OTP=123456

echo 🚀 Starting Emoji Sphere Authentication API Tests
echo ==================================================

REM Test 1: Validate Mobile Number
echo 📱 Test 1: Validate Mobile Number
curl -X POST "%BASE_URL%/validate-mobile" -H "Content-Type: application/json" -d "{\"mobile\": \"%MOBILE%\"}"
echo.

REM Test 2: Send OTP
echo 📨 Test 2: Send OTP
curl -X POST "%BASE_URL%/send-otp" -H "Content-Type: application/json" -d "{\"mobile\": \"%MOBILE%\"}"
echo.

REM Test 3: Verify OTP
echo ✅ Test 3: Verify OTP
curl -X POST "%BASE_URL%/verify-otp" -H "Content-Type: application/json" -d "{\"mobile\": \"%MOBILE%\", \"otp\": \"%OTP%\"}"
echo.

REM Test 4: Check Email Availability
echo 📧 Test 4: Check Email Availability
curl -X POST "%BASE_URL%/check-email" -H "Content-Type: application/json" -d "\"%EMAIL%\""
echo.

REM Test 5: Sign Up
echo 👤 Test 5: User Registration (Sign Up)
curl -X POST "%BASE_URL%/signup" -H "Content-Type: application/json" -d "{\"fullName\": \"John Doe\", \"mobile\": \"%MOBILE%\", \"email\": \"%EMAIL%\", \"password\": \"%PASSWORD%\", \"confirmPassword\": \"%PASSWORD%\", \"age\": 25, \"country\": \"United States\", \"gender\": \"Male\"}"
echo.

REM Test 6: Sign In
echo 🔑 Test 6: User Login (Sign In)
curl -X POST "%BASE_URL%/signin" -H "Content-Type: application/json" -d "{\"mobile\": \"%MOBILE%\", \"password\": \"%PASSWORD%\"}"
echo.

REM Test 7: Forgot Password
echo 🔒 Test 7: Forgot Password
curl -X POST "%BASE_URL%/forgot-password" -H "Content-Type: application/json" -d "{\"mobile\": \"%MOBILE%\"}"
echo.

REM Test 8: Reset Password
echo 🔄 Test 8: Reset Password
curl -X POST "%BASE_URL%/reset-password" -H "Content-Type: application/json" -d "{\"mobile\": \"%MOBILE%\", \"otp\": \"%OTP%\", \"newPassword\": \"%NEW_PASSWORD%\", \"confirmPassword\": \"%NEW_PASSWORD%\"}"
echo.

REM Test 9: Sign In with New Password
echo 🔐 Test 9: Sign In with New Password
curl -X POST "%BASE_URL%/signin" -H "Content-Type: application/json" -d "{\"mobile\": \"%MOBILE%\", \"password\": \"%NEW_PASSWORD%\"}"
echo.

echo ==================================================
echo ✨ All tests completed!
echo 💡 Note: Some tests may fail if:
echo    - The backend server is not running
echo    - Database is not set up correctly
echo    - OTP service is not configured
echo    - User already exists in the database

pause