@echo off
echo Testing User Profile APIs...

set BASE_URL=http://localhost:8080
set /p MOBILE=Enter mobile number for testing: 
set /p PASSWORD=Enter password: 

echo.
echo ==========================================
echo 1. Testing Login to get JWT token...
echo ==========================================

curl -X POST "%BASE_URL%/auth/signin" ^
  -H "Content-Type: application/json" ^
  -d "{\"mobile\":\"%MOBILE%\",\"password\":\"%PASSWORD%\"}" ^
  -w "\nStatus Code: %%{http_code}\n" ^
  -o login_response.json

echo.
echo Login Response:
type login_response.json

:: Extract JWT token from response (manual step for Windows batch)
echo.
echo Please copy the JWT token from the above response and paste it below:
set /p JWT_TOKEN=JWT Token: 

echo.
echo ==========================================
echo 2. Testing Get User Profile...
echo ==========================================

curl -X GET "%BASE_URL%/api/user/profile" ^
  -H "Authorization: Bearer %JWT_TOKEN%" ^
  -H "Content-Type: application/json" ^
  -w "\nStatus Code: %%{http_code}\n"

echo.
echo ==========================================
echo 3. Testing Update User Profile (with school name)...
echo ==========================================

curl -X PUT "%BASE_URL%/api/user/profile" ^
  -H "Authorization: Bearer %JWT_TOKEN%" ^
  -H "Content-Type: application/json" ^
  -d "{\"schoolName\":\"Test University\",\"country\":\"Test Country\"}" ^
  -w "\nStatus Code: %%{http_code}\n"

echo.
echo ==========================================
echo 4. Testing Get User Profile After Update...
echo ==========================================

curl -X GET "%BASE_URL%/api/user/profile" ^
  -H "Authorization: Bearer %JWT_TOKEN%" ^
  -H "Content-Type: application/json" ^
  -w "\nStatus Code: %%{http_code}\n"

echo.
echo ==========================================
echo 5. Testing Signup with School Name...
echo ==========================================

echo Enter details for new user signup test:
set /p NEW_MOBILE=New Mobile Number: 
set /p NEW_NAME=Full Name: 
set /p NEW_EMAIL=Email: 
set /p NEW_PASSWORD=Password: 
set /p SCHOOL_NAME=School Name: 

echo First, sending OTP...
curl -X POST "%BASE_URL%/auth/send-otp" ^
  -H "Content-Type: application/json" ^
  -d "{\"mobile\":\"%NEW_MOBILE%\"}" ^
  -w "\nStatus Code: %%{http_code}\n"

echo.
set /p OTP=Enter the OTP you received: 

echo Verifying OTP...
curl -X POST "%BASE_URL%/auth/verify-otp" ^
  -H "Content-Type: application/json" ^
  -d "{\"mobile\":\"%NEW_MOBILE%\",\"otp\":\"%OTP%\"}" ^
  -w "\nStatus Code: %%{http_code}\n"

echo.
echo Now testing signup with school name...
curl -X POST "%BASE_URL%/auth/signup" ^
  -H "Content-Type: application/json" ^
  -d "{\"fullName\":\"%NEW_NAME%\",\"mobile\":\"%NEW_MOBILE%\",\"email\":\"%NEW_EMAIL%\",\"password\":\"%NEW_PASSWORD%\",\"confirmPassword\":\"%NEW_PASSWORD%\",\"age\":25,\"country\":\"Test Country\",\"gender\":\"Male\",\"schoolName\":\"%SCHOOL_NAME%\"}" ^
  -w "\nStatus Code: %%{http_code}\n"

echo.
echo ==========================================
echo Testing completed!
echo ==========================================

:: Cleanup
del login_response.json

pause