#!/bin/bash

# Parking Management System Setup Script
# This script helps set up the parking management system quickly

echo "🅿️  Parking Management System Setup"
echo "=================================="

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 17 or higher."
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "❌ Java version $JAVA_VERSION is not supported. Please install Java 17 or higher."
    exit 1
fi

echo "✅ Java $JAVA_VERSION detected"

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please install Maven 3.6 or higher."
    exit 1
fi

echo "✅ Maven detected"

# Create logs directory
mkdir -p logs

# Compile the project
echo "🔨 Compiling the project..."
mvn clean compile

if [ $? -eq 0 ]; then
    echo "✅ Project compiled successfully"
else
    echo "❌ Compilation failed"
    exit 1
fi

# Run tests
echo "🧪 Running tests..."
mvn test

if [ $? -eq 0 ]; then
    echo "✅ All tests passed"
else
    echo "⚠️  Some tests failed, but continuing with setup"
fi

# Create application-local.properties if it doesn't exist
if [ ! -f "src/main/resources/application-local.properties" ]; then
    echo "📝 Creating local configuration file..."
    cat > src/main/resources/application-local.properties << EOF
# Local development configuration
spring.profiles.active=local

# OAuth2 Configuration (Update with your credentials)
spring.security.oauth2.client.registration.google.client-id=YOUR_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_CLIENT_SECRET

# Admin email
admin.emails=your-email@gmail.com

# Database configuration
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# Enable H2 console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Logging
logging.level.com.demo.parkinglot=INFO
logging.file.name=logs/parking-system.log
EOF
    echo "✅ Local configuration file created"
    echo "⚠️  Please update src/main/resources/application-local.properties with your OAuth2 credentials"
fi

echo ""
echo "🎉 Setup completed successfully!"
echo ""
echo "Next steps:"
echo "1. Update OAuth2 credentials in src/main/resources/application-local.properties"
echo "2. Update admin email in the same file"
echo "3. Run: mvn spring-boot:run -Dspring.profiles.active=local"
echo "4. Access the application at: http://localhost:8080"
echo "5. Access H2 console at: http://localhost:8080/h2-console"
echo ""
echo "For more information, see README.md"
