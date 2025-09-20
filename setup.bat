@echo off
REM Parking Management System Setup Script for Windows
REM This script helps set up the parking management system quickly

echo 🅿️  Parking Management System Setup
echo ==================================

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Java is not installed. Please install Java 17 or higher.
    pause
    exit /b 1
)

echo ✅ Java detected

REM Check if Maven is installed
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Maven is not installed. Please install Maven 3.6 or higher.
    pause
    exit /b 1
)

echo ✅ Maven detected

REM Create logs directory
if not exist logs mkdir logs

REM Compile the project
echo 🔨 Compiling the project...
mvn clean compile

if %errorlevel% equ 0 (
    echo ✅ Project compiled successfully
) else (
    echo ❌ Compilation failed
    pause
    exit /b 1
)

REM Run tests
echo 🧪 Running tests...
mvn test

if %errorlevel% equ 0 (
    echo ✅ All tests passed
) else (
    echo ⚠️  Some tests failed, but continuing with setup
)

REM Create application-local.properties if it doesn't exist
if not exist "src\main\resources\application-local.properties" (
    echo 📝 Creating local configuration file...
    (
        echo # Local development configuration
        echo spring.profiles.active=local
        echo.
        echo # OAuth2 Configuration ^(Update with your credentials^)
        echo spring.security.oauth2.client.registration.google.client-id=YOUR_CLIENT_ID
        echo spring.security.oauth2.client.registration.google.client-secret=YOUR_CLIENT_SECRET
        echo.
        echo # Admin email
        echo admin.emails=your-email@gmail.com
        echo.
        echo # Database configuration
        echo spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
        echo spring.datasource.driverClassName=org.h2.Driver
        echo spring.datasource.username=sa
        echo spring.datasource.password=
        echo.
        echo # Enable H2 console
        echo spring.h2.console.enabled=true
        echo spring.h2.console.path=/h2-console
        echo.
        echo # Logging
        echo logging.level.com.demo.parkinglot=INFO
        echo logging.file.name=logs/parking-system.log
    ) > "src\main\resources\application-local.properties"
    echo ✅ Local configuration file created
    echo ⚠️  Please update src\main\resources\application-local.properties with your OAuth2 credentials
)

echo.
echo 🎉 Setup completed successfully!
echo.
echo Next steps:
echo 1. Update OAuth2 credentials in src\main\resources\application-local.properties
echo 2. Update admin email in the same file
echo 3. Run: mvn spring-boot:run -Dspring.profiles.active=local
echo 4. Access the application at: http://localhost:8080
echo 5. Access H2 console at: http://localhost:8080/h2-console
echo.
echo For more information, see README.md
pause
