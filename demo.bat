@echo off
REM Parking Management System Demo Script for Windows
REM This script demonstrates the key features of the parking management system

echo 🅿️  Parking Management System Demo
echo =================================
echo.

REM Check if application is running
curl -s http://localhost:8080/api/health >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Application is not running. Please start it first:
    echo    mvn spring-boot:run
    pause
    exit /b 1
)

echo ✅ Application is running
echo.

REM Demo 1: Health Check
echo 🔍 Demo 1: Health Check
echo ----------------------
curl -s http://localhost:8080/api/health
echo.
echo.

REM Demo 2: Application Info
echo 📊 Demo 2: Application Information
echo --------------------------------
curl -s http://localhost:8080/api/info
echo.
echo.

REM Demo 3: Get Entry Gates
echo 🚪 Demo 3: Available Entry Gates
echo -------------------------------
curl -s http://localhost:8080/api/entry-gates
echo.
echo.

REM Demo 4: Parking Lot Status
echo 📈 Demo 4: Parking Lot Status
echo ----------------------------
curl -s http://localhost:8080/api/parking-lot/1/status
echo.
echo.

echo 🎯 Demo completed!
echo.
echo To test the full functionality:
echo 1. Set up OAuth2 authentication
echo 2. Use the Postman collection: api-testing\Parking-Management-System.postman_collection.json
echo 3. Follow the testing guide: TESTING.md
echo.
echo For more information, see README.md
pause
