# 🧪 Testing Guide - Parking Management System

This guide provides comprehensive testing instructions for the Parking Management System.

## 📋 Test Categories

### 1. Unit Tests
- **Location**: `src/test/java/com/demo/parkinglot/service/`
- **Coverage**: Service layer methods, utility functions, strategy implementations
- **Run Command**: `mvn test`

### 2. Integration Tests
- **Location**: `src/test/java/com/demo/parkinglot/integration/`
- **Coverage**: End-to-end parking flows, database interactions
- **Run Command**: `mvn verify`

### 3. Manual API Testing
- **Tools**: Postman, cURL, or any REST client
- **Collection**: `api-testing/Parking-Management-System.postman_collection.json`

## 🚀 Quick Test Setup

### Prerequisites
1. Application running on `http://localhost:8080`
2. OAuth2 credentials configured
3. Admin user email configured

### Test Data Setup
The application automatically creates test data on startup. You can also manually create data using the admin APIs.

## 📝 Manual Testing Scenarios

### Scenario 1: Basic Parking Flow

#### Step 1: Park a Car
```bash
curl -X POST http://localhost:8080/api/entry \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "plateNo": "TEST-001",
    "vehicleType": "CAR",
    "ownerId": "user123",
    "entryGateId": 1
  }'
```

**Expected Result**: 
- Status: 200 OK
- Response contains ticket ID, slot number, entry time
- Slot becomes unavailable

#### Step 2: Check Parking Lot Status
```bash
curl -X GET http://localhost:8080/api/parking-lot/1/status \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Expected Result**:
- Shows occupied slots count
- Displays floor-wise availability

#### Step 3: Exit with Payment
```bash
curl -X POST http://localhost:8080/api/exit/{ticketId} \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "amount": 4.0
  }'
```

**Expected Result**:
- Status: 200 OK
- Response contains payment and receipt details
- Slot becomes available again

### Scenario 2: Error Handling

#### Test 1: Duplicate Vehicle Entry
```bash
# First entry
curl -X POST http://localhost:8080/api/entry \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "plateNo": "DUP-001",
    "vehicleType": "CAR",
    "ownerId": "user123",
    "entryGateId": 1
  }'

# Second entry (should fail)
curl -X POST http://localhost:8080/api/entry \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "plateNo": "DUP-001",
    "vehicleType": "CAR",
    "ownerId": "user123",
    "entryGateId": 1
  }'
```

**Expected Result**: Second request returns 409 Conflict

#### Test 2: Payment with Wrong Amount
```bash
curl -X POST http://localhost:8080/api/exit/{ticketId} \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "amount": 1.0
  }'
```

**Expected Result**: Returns 400 Bad Request with amount mismatch error

#### Test 3: Access Admin Endpoints Without Admin Role
```bash
curl -X GET http://localhost:8080/api/admin/pricing-rules \
  -H "Authorization: Bearer USER_TOKEN"
```

**Expected Result**: Returns 403 Forbidden

### Scenario 3: Admin Operations

#### Test 1: Update Pricing Rules
```bash
curl -X PUT http://localhost:8080/api/admin/pricing-rules \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ADMIN_TOKEN" \
  -d '{
    "vehicleType": "CAR",
    "hourlyRate": 3.0,
    "description": "Peak hour rate"
  }'
```

**Expected Result**: Returns 200 OK with success message

#### Test 2: Add Parking Slots
```bash
curl -X POST http://localhost:8080/api/admin/slots \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ADMIN_TOKEN" \
  -d '{
    "parkingLotId": 1,
    "floor": 1,
    "slotType": "CAR",
    "slotNumber": "A-201",
    "xCoordinate": 30.0,
    "yCoordinate": 30.0
  }'
```

**Expected Result**: Returns 200 OK with slot creation confirmation

#### Test 3: Get Statistics
```bash
curl -X GET http://localhost:8080/api/admin/slots/statistics \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

**Expected Result**: Returns detailed statistics including occupancy rates

### Scenario 4: Concurrency Testing

#### Test Concurrent Slot Allocation
```bash
# Run multiple parking requests simultaneously
for i in {1..5}; do
  curl -X POST http://localhost:8080/api/entry \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer YOUR_TOKEN" \
    -d "{
      \"plateNo\": \"CONC-$i\",
      \"vehicleType\": \"CAR\",
      \"ownerId\": \"user$i\",
      \"entryGateId\": 1
    }" &
done
wait
```

**Expected Result**: All requests should succeed with different slot assignments

## 🔍 Performance Testing

### Load Testing with Apache Bench
```bash
# Test entry endpoint
ab -n 100 -c 10 -H "Authorization: Bearer YOUR_TOKEN" \
   -H "Content-Type: application/json" \
   -p entry_request.json \
   http://localhost:8080/api/entry

# Test status endpoint
ab -n 1000 -c 50 -H "Authorization: Bearer YOUR_TOKEN" \
   http://localhost:8080/api/parking-lot/1/status
```

### Memory Testing
```bash
# Monitor memory usage during load
jstat -gc -t $(pgrep java) 1s
```

## 🐛 Debugging

### Enable Debug Logging
Add to `application.properties`:
```properties
logging.level.com.demo.parkinglot=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

### Database Inspection
1. Access H2 Console: http://localhost:8080/h2-console
2. JDBC URL: `jdbc:h2:mem:testdb`
3. Username: `sa`
4. Password: (empty)

### Common Issues and Solutions

#### Issue 1: OAuth2 Authentication Fails
**Symptoms**: 401 Unauthorized, redirect to login
**Solution**: 
- Verify Google OAuth2 credentials
- Check redirect URI configuration
- Ensure user email is in admin list

#### Issue 2: Slot Allocation Fails
**Symptoms**: 409 Conflict, "Unable to allocate slot"
**Solution**:
- Check if parking lot is full
- Verify vehicle type matches available slots
- Check for concurrent allocation conflicts

#### Issue 3: Payment Processing Fails
**Symptoms**: 400 Bad Request, amount mismatch
**Solution**:
- Calculate correct amount based on duration
- Check pricing configuration
- Verify payment amount format

#### Issue 4: Database Connection Issues
**Symptoms**: 500 Internal Server Error, connection refused
**Solution**:
- Check database configuration
- Ensure H2 is properly configured
- Verify database URL and credentials

## 📊 Test Coverage

### Unit Test Coverage
- **SlotAllocationManager**: 15+ test cases
- **PaymentService**: 10+ test cases
- **AdminService**: 8+ test cases
- **Utility Classes**: 5+ test cases

### Integration Test Coverage
- **Complete Parking Flow**: Entry → Payment → Exit
- **Error Scenarios**: Payment failures, duplicate entries
- **Concurrency**: Multiple simultaneous operations
- **Admin Operations**: Pricing and slot management

### API Test Coverage
- **Authentication**: OAuth2 flow
- **Parking Operations**: All CRUD operations
- **Admin Operations**: Management functions
- **Error Handling**: All error scenarios

## 🎯 Test Automation

### Continuous Integration
```yaml
# .github/workflows/test.yml
name: Test Suite
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 17
        uses: actions/setup-java@v2
        with:
          java-version: '17'
          distribution: 'temurin'
      - name: Run tests
        run: mvn test
      - name: Generate coverage report
        run: mvn jacoco:report
```

### Test Data Management
- Use `@Sql` annotations for test data setup
- Clean up test data after each test
- Use test-specific profiles

## 📈 Monitoring and Metrics

### Application Metrics
- Response times for each endpoint
- Error rates and types
- Database connection pool status
- Memory and CPU usage

### Business Metrics
- Slot occupancy rates
- Revenue per hour/day
- Peak usage times
- Vehicle type distribution

## 🚀 Production Testing

### Smoke Tests
1. Health check endpoint responds
2. Database connectivity works
3. OAuth2 authentication flows
4. Basic parking operations

### Load Tests
1. Concurrent user simulation
2. Database performance under load
3. Memory usage patterns
4. Response time degradation

### Security Tests
1. Authentication bypass attempts
2. Authorization boundary testing
3. Input validation testing
4. SQL injection prevention

---

**Happy Testing! 🧪**
