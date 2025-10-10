@echo off
echo Testing Friendship APIs...

set BASE_URL=http://localhost:8080
set /p MOBILE1=Enter first user mobile number for testing: 
set /p PASSWORD1=Enter first user password: 
set /p MOBILE2=Enter second user mobile number for testing: 
set /p PASSWORD2=Enter second user password: 

echo.
echo ==========================================
echo 1. Testing Login for User 1...
echo ==========================================

curl -X POST "%BASE_URL%/auth/signin" ^
  -H "Content-Type: application/json" ^
  -d "{\"mobile\":\"%MOBILE1%\",\"password\":\"%PASSWORD1%\"}" ^
  -w "\nStatus Code: %%{http_code}\n" ^
  -o login1_response.json

echo.
echo User 1 Login Response:
type login1_response.json

echo.
echo Please copy the JWT token for User 1 from the above response:
set /p JWT_TOKEN1=User 1 JWT Token: 

echo.
echo ==========================================
echo 2. Testing Login for User 2...
echo ==========================================

curl -X POST "%BASE_URL%/auth/signin" ^
  -H "Content-Type: application/json" ^
  -d "{\"mobile\":\"%MOBILE2%\",\"password\":\"%PASSWORD2%\"}" ^
  -w "\nStatus Code: %%{http_code}\n" ^
  -o login2_response.json

echo.
echo User 2 Login Response:
type login2_response.json

echo.
echo Please copy the JWT token for User 2 from the above response:
set /p JWT_TOKEN2=User 2 JWT Token: 

echo.
echo ==========================================
echo 3. Get User 1 Profile to get User ID...
echo ==========================================

curl -X GET "%BASE_URL%/api/user/profile" ^
  -H "Authorization: Bearer %JWT_TOKEN1%" ^
  -H "Content-Type: application/json" ^
  -w "\nStatus Code: %%{http_code}\n" ^
  -o user1_profile.json

echo.
echo User 1 Profile:
type user1_profile.json

echo.
echo ==========================================
echo 4. Get User 2 Profile to get User ID...
echo ==========================================

curl -X GET "%BASE_URL%/api/user/profile" ^
  -H "Authorization: Bearer %JWT_TOKEN2%" ^
  -H "Content-Type: application/json" ^
  -w "\nStatus Code: %%{http_code}\n" ^
  -o user2_profile.json

echo.
echo User 2 Profile:
type user2_profile.json

echo.
echo Please enter the User IDs from the profiles above:
set /p USER1_ID=User 1 ID: 
set /p USER2_ID=User 2 ID: 

echo.
echo ==========================================
echo 5. User 1 sends friend request to User 2...
echo ==========================================

curl -X POST "%BASE_URL%/api/friendships/send-request" ^
  -H "Authorization: Bearer %JWT_TOKEN1%" ^
  -H "Content-Type: application/json" ^
  -d "{\"targetUserId\":%USER2_ID%}" ^
  -w "\nStatus Code: %%{http_code}\n"

echo.
echo ==========================================
echo 6. User 2 checks pending requests received...
echo ==========================================

curl -X GET "%BASE_URL%/api/friendships/pending/received" ^
  -H "Authorization: Bearer %JWT_TOKEN2%" ^
  -H "Content-Type: application/json" ^
  -w "\nStatus Code: %%{http_code}\n" ^
  -o user2_pending.json

echo.
echo User 2 Pending Requests:
type user2_pending.json

echo.
echo Please enter the friendship ID from the pending request above:
set /p FRIENDSHIP_ID=Friendship ID: 

echo.
echo ==========================================
echo 7. User 2 accepts the friend request...
echo ==========================================

curl -X POST "%BASE_URL%/api/friendships/respond" ^
  -H "Authorization: Bearer %JWT_TOKEN2%" ^
  -H "Content-Type: application/json" ^
  -d "{\"friendshipId\":%FRIENDSHIP_ID%,\"response\":\"ACCEPTED\"}" ^
  -w "\nStatus Code: %%{http_code}\n"

echo.
echo ==========================================
echo 8. User 1 checks friends list...
echo ==========================================

curl -X GET "%BASE_URL%/api/friendships/friends" ^
  -H "Authorization: Bearer %JWT_TOKEN1%" ^
  -H "Content-Type: application/json" ^
  -w "\nStatus Code: %%{http_code}\n"

echo.
echo ==========================================
echo 9. User 2 checks friends list...
echo ==========================================

curl -X GET "%BASE_URL%/api/friendships/friends" ^
  -H "Authorization: Bearer %JWT_TOKEN2%" ^
  -H "Content-Type: application/json" ^
  -w "\nStatus Code: %%{http_code}\n"

echo.
echo ==========================================
echo 10. Check friendship status between users...
echo ==========================================

curl -X GET "%BASE_URL%/api/friendships/status/%USER2_ID%" ^
  -H "Authorization: Bearer %JWT_TOKEN1%" ^
  -H "Content-Type: application/json" ^
  -w "\nStatus Code: %%{http_code}\n"

echo.
echo ==========================================
echo 11. Get friendship counts for User 1...
echo ==========================================

curl -X GET "%BASE_URL%/api/friendships/counts" ^
  -H "Authorization: Bearer %JWT_TOKEN1%" ^
  -H "Content-Type: application/json" ^
  -w "\nStatus Code: %%{http_code}\n"

echo.
echo ==========================================
echo 12. Testing remove friend (User 1 removes User 2)...
echo ==========================================

echo Do you want to test removing the friendship? (y/n)
set /p REMOVE_CONFIRM=Remove friendship: 

if /i "%REMOVE_CONFIRM%"=="y" (
    curl -X DELETE "%BASE_URL%/api/friendships/remove/%USER2_ID%" ^
      -H "Authorization: Bearer %JWT_TOKEN1%" ^
      -H "Content-Type: application/json" ^
      -w "\nStatus Code: %%{http_code}\n"
    
    echo.
    echo Checking friends list after removal...
    curl -X GET "%BASE_URL%/api/friendships/friends" ^
      -H "Authorization: Bearer %JWT_TOKEN1%" ^
      -H "Content-Type: application/json" ^
      -w "\nStatus Code: %%{http_code}\n"
)

echo.
echo ==========================================
echo 13. Testing block user functionality...
echo ==========================================

echo Do you want to test blocking User 2? (y/n)
set /p BLOCK_CONFIRM=Block user: 

if /i "%BLOCK_CONFIRM%"=="y" (
    curl -X POST "%BASE_URL%/api/friendships/block/%USER2_ID%" ^
      -H "Authorization: Bearer %JWT_TOKEN1%" ^
      -H "Content-Type: application/json" ^
      -w "\nStatus Code: %%{http_code}\n"
    
    echo.
    echo Checking friendship status after blocking...
    curl -X GET "%BASE_URL%/api/friendships/status/%USER2_ID%" ^
      -H "Authorization: Bearer %JWT_TOKEN1%" ^
      -H "Content-Type: application/json" ^
      -w "\nStatus Code: %%{http_code}\n"
    
    echo.
    echo Testing unblock...
    curl -X POST "%BASE_URL%/api/friendships/unblock/%USER2_ID%" ^
      -H "Authorization: Bearer %JWT_TOKEN1%" ^
      -H "Content-Type: application/json" ^
      -w "\nStatus Code: %%{http_code}\n"
)

echo.
echo ==========================================
echo Testing completed!
echo ==========================================

:: Cleanup
del login1_response.json
del login2_response.json
del user1_profile.json
del user2_profile.json
del user2_pending.json

pause