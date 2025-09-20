# 🅿️ Parking Management System

A comprehensive, enterprise-grade parking management system built with Spring Boot, featuring multi-floor parking, real-time slot allocation, payment processing, and admin management capabilities.

## 🚀 Features

### Core Functionality
- **Multi-Floor Parking**: Support for unlimited floors with individual capacity management
- **Smart Slot Allocation**: Multiple allocation strategies (Nearest, First Available, Level-wise)
- **Real-time Capacity Management**: Live occupancy tracking and full-lot prevention
- **Payment Processing**: Atomic payment handling with failure recovery
- **Ticket Management**: Complete entry/exit flow with receipt generation

### Vehicle Support
- **Multiple Vehicle Types**: Cars, Bikes, Trucks with type-specific pricing
- **Slot Type Enforcement**: Prevents mismatched vehicle-slot assignments
- **Configurable Pricing**: Dynamic hourly rates per vehicle type

### Security & Access Control
- **OAuth2 Integration**: Google OAuth2 authentication
- **Role-Based Access**: Admin and User role separation
- **API Security**: Protected endpoints with proper authorization

### Admin Capabilities
- **Pricing Management**: Configure hourly rates for different vehicle types
- **Slot Management**: Add, remove, and update parking slots
- **Analytics Dashboard**: Real-time statistics and occupancy reports
- **Bulk Operations**: Efficient slot management operations

## 🛠️ Technology Stack

- **Backend**: Spring Boot 3.x, Spring Security, Spring Data JPA
- **Database**: H2 (In-Memory) for development, easily configurable for production
- **Authentication**: OAuth2 with Google
- **Build Tool**: Maven
- **Java Version**: 17+

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- Git
- Google OAuth2 credentials (for authentication)

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone <repository-url>
cd Parking-Management-System
```

### 2. Configure Google OAuth2
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing one
3. Enable Google+ API
4. Create OAuth2 credentials
5. Update `application.properties` with your credentials:

```properties
# OAuth2 Client Configuration
spring.security.oauth2.client.registration.google.client-id=YOUR_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_CLIENT_SECRET
```

### 3. Configure Admin Users
Update the admin email in `application.properties`:
```properties
admin.emails=your-admin-email@gmail.com
```

### 4. Build and Run
```bash
# Build the project
mvn clean compile

# Run the application
mvn spring-boot:run
```

### 5. Access the Application
- **Application**: http://localhost:8080
- **H2 Database Console**: http://localhost:8080/h2-console
- **API Documentation**: http://localhost:8080/swagger-ui.html

## 🔧 Configuration

### Database Configuration
The application uses H2 in-memory database by default. To use a different database:

```properties
# PostgreSQL Example
spring.datasource.url=jdbc:postgresql://localhost:5432/parking_db
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

### Parking Charges Configuration
```properties
# Hourly rates for different vehicle types
parking.charges.hourly-rates.CAR=2.0
parking.charges.hourly-rates.BIKE=1.0
parking.charges.hourly-rates.TRUCK=5.0
```

### Allocation Strategy Configuration
```properties
# Slot allocation strategy
parking.allocation.strategy=NEAREST_SLOT
parking.allocation.enable-strategy-switching=true
parking.allocation.log-strategy-usage=false
```

## 📚 API Documentation

### Authentication Endpoints
- `GET /login` - OAuth2 login
- `GET /logout` - Logout

### Public Parking Operations
- `POST /api/entry` - Park a vehicle
- `POST /api/exit/{ticketId}` - Exit with payment
- `POST /api/exit/{ticketId}/retry` - Retry failed payment
- `GET /api/entry-gates` - Get available entry gates
- `GET /api/parking-lot/{id}/status` - Get parking lot status

### Admin Operations (Requires ADMIN role)
- `PUT /api/admin/pricing-rules` - Update pricing rules
- `GET /api/admin/pricing-rules` - Get current pricing rules
- `POST /api/admin/slots` - Add parking slot
- `DELETE /api/admin/slots/{id}` - Remove parking slot
- `PUT /api/admin/slots/{id}` - Update parking slot
- `POST /api/admin/slots/bulk` - Bulk add slots
- `GET /api/admin/slots/statistics` - Get slot statistics
- `GET /api/admin/parking-lots/{id}/overview` - Get parking lot overview

### Health & Info Endpoints
- `GET /api/health` - Application health check
- `GET /api/info` - Application information

## 🧪 Testing

### Run Unit Tests
```bash
mvn test
```

### Run Integration Tests
```bash
mvn verify
```

### Test Coverage
```bash
mvn jacoco:report
```

### Manual Testing with cURL

#### 1. Park a Vehicle
```bash
curl -X POST http://localhost:8080/api/entry \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "plateNo": "ABC-123",
    "vehicleType": "CAR",
    "ownerId": "user123",
    "entryGateId": 1
  }'
```

#### 2. Exit with Payment
```bash
curl -X POST http://localhost:8080/api/exit/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "amount": 4.0
  }'
```

#### 3. Get Parking Lot Status
```bash
curl -X GET http://localhost:8080/api/parking-lot/1/status \
  -H "Authorization: Bearer YOUR_TOKEN"
```

#### 4. Admin: Update Pricing Rules
```bash
curl -X PUT http://localhost:8080/api/admin/pricing-rules \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN" \
  -d '{
    "vehicleType": "CAR",
    "hourlyRate": 3.0,
    "description": "Updated car rate"
  }'
```

## 🏗️ Project Structure

```
src/
├── main/
│   ├── java/com/demo/parkinglot/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── entity/         # JPA entities
│   │   ├── enums/          # Enumerations
│   │   ├── exception/      # Custom exceptions
│   │   ├── oauth/          # OAuth2 configuration
│   │   ├── repository/     # Data repositories
│   │   ├── service/        # Business logic services
│   │   ├── strategy/       # Allocation strategies
│   │   └── util/           # Utility classes
│   └── resources/
│       └── application.properties
└── test/
    ├── java/               # Test classes
    └── resources/
        └── application-test.properties
```

## 🔒 Security Features

### Authentication Flow
1. User accesses protected endpoint
2. Redirected to Google OAuth2 login
3. After successful authentication, user gets JWT token
4. Token used for subsequent API calls

### Role-Based Access Control
- **USER Role**: Can park/retrieve vehicles, view parking status
- **ADMIN Role**: Can manage pricing, slots, and view analytics

### Data Protection
- All sensitive operations are transactionally protected
- Pessimistic locking prevents concurrent slot allocation
- Payment processing is atomic with rollback on failure

## 🚀 Deployment

### Docker Deployment
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/parking-management-system-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Production Configuration
1. Update database configuration for production DB
2. Configure proper OAuth2 credentials
3. Set up monitoring and logging
4. Configure security headers
5. Set up load balancing if needed

## 📊 Monitoring & Analytics

### Health Checks
- Application health status
- Database connectivity
- External service dependencies

### Metrics Available
- Slot occupancy rates
- Revenue tracking
- Peak usage times
- Vehicle type distribution

## 🐛 Troubleshooting

### Common Issues

#### 1. OAuth2 Authentication Fails
- Verify Google OAuth2 credentials
- Check redirect URI configuration
- Ensure Google+ API is enabled

#### 2. Database Connection Issues
- Check database URL and credentials
- Ensure database server is running
- Verify network connectivity

#### 3. Slot Allocation Failures
- Check if parking lot is full
- Verify vehicle type matches slot type
- Check for concurrent allocation conflicts

#### 4. Payment Processing Errors
- Verify payment amount matches calculated amount
- Check payment gateway configuration
- Review transaction logs

### Logs
Application logs are available in the console. For production, configure proper logging:

```properties
logging.level.com.demo.parkinglot=DEBUG
logging.level.org.springframework.security=WARN
logging.file.name=logs/parking-system.log
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Support

For support and questions:
- Create an issue in the repository
- Check the troubleshooting section
- Review the API documentation

## 🎯 Future Enhancements

- [ ] Mobile app integration
- [ ] Real-time notifications
- [ ] Advanced analytics dashboard
- [ ] Multi-tenant support
- [ ] Integration with payment gateways
- [ ] Automated testing with Selenium
- [ ] Performance monitoring with Micrometer

---

**Happy Parking! 🅿️**