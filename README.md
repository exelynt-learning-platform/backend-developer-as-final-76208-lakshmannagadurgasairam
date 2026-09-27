# Resource Booking API

A secure RESTful Resource Booking System built using **Spring Boot, Java 17, Spring Security, JWT Authentication, Spring Data JPA, Hibernate, and MySQL**.

The application provides secure resource and reservation management with **JWT authentication and role-based access control (RBAC)**.

---

## 1. Project Overview

The Resource Booking API allows authenticated users to view available resources and create reservations.

The system supports two roles:

- **ADMIN** – Can manage resources and reservations.
- **USER** – Can view resources, create reservations, and view only their own reservations.

The application uses JWT for stateless authentication and Spring Security for authorization.

### Main Domain Objects

**Resource**

A bookable item such as:

- Room
- Vehicle
- Equipment

**Reservation**

A booking made by an authenticated user for a resource.

---

## 2. Technologies Used

| Technology | Version / Purpose |
|---|---|
| Java | 17 |
| Spring Boot | 3.2.3 |
| Spring Security | Authentication & Authorization |
| JWT | Token-based authentication |
| Spring Data JPA | Database access |
| Hibernate | ORM |
| MySQL | Database |
| Maven | Build and dependency management |
| Swagger / OpenAPI | API documentation |
| Lombok | Boilerplate reduction |
| Bean Validation | Request validation |

---

## 3. Key Features

### Authentication

- JWT-based login
- BCrypt password hashing
- Stateless authentication
- Token validation using Spring Security
- Protected API endpoints

### Authorization

- ADMIN and USER roles
- Role-based endpoint access
- USER can access only their own reservations
- ADMIN can access all reservations
- Unauthorized requests return appropriate HTTP status codes

### Resource Management

- Create resource
- View all resources
- View resource by ID
- Update resource
- Delete resource
- Pagination support

### Reservation Management

- Create reservation
- View reservations
- ADMIN can view all reservations
- USER can view only their own reservations
- Update reservation status
- Delete reservation
- Reservation status support
- Price filtering
- Status filtering
- Pagination
- Sorting

### Validation

- Required field validation
- Price validation
- Resource existence validation
- Reservation time validation
- Start/end time validation
- Invalid requests return HTTP 400

### Error Handling

The application provides centralized exception handling for:

- Resource not found
- Invalid credentials
- Access denied
- Validation errors
- Unexpected server errors

---

## 4. Project Structure

```text
resource-booking-api/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com.example.booking/
│   │   │       ├── config/
│   │   │       │   ├── SecurityConfig.java
│   │   │       │   ├── JwtService.java
│   │   │       │   ├── JwtAuthenticationFilter.java
│   │   │       │   └── CustomUserDetailsService.java
│   │   │       │
│   │   │       ├── controller/
│   │   │       │   ├── AuthController.java
│   │   │       │   ├── ResourceController.java
│   │   │       │   └── ReservationController.java
│   │   │       │
│   │   │       ├── dto/
│   │   │       │
│   │   │       ├── entity/
│   │   │       │
│   │   │       ├── exception/
│   │   │       │   ├── GlobalExceptionHandler.java
│   │   │       │   └── ResourceNotFoundException.java
│   │   │       │
│   │   │       ├── repository/
│   │   │       │
│   │   │       ├── service/
│   │   │       │   ├── ResourceService.java
│   │   │       │   └── ReservationService.java
│   │   │       │
│   │   │       └── ResourceBookingApiApplication.java
│   │   │
│   │   └── resources/
│   │       └── application.properties
│   │
│   └── test/
│
├── pom.xml
├── README.md
└── .gitignore
```

---

## 5. Prerequisites

Before running the application, install:

- Java 17 or higher
- Maven
- MySQL
- Git

Verify Java:

```bash
java -version
```

Verify Maven:

```bash
mvn -version
```

---

## 6. Database Configuration

The application uses MySQL.

Create the database:

```sql
CREATE DATABASE booking_db;
```

The application can also create the database automatically because the JDBC URL contains:

```text
createDatabaseIfNotExist=true
```

### Database Configuration

The current local development configuration is:

```properties
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/booking_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata
spring.datasource.username=root
spring.datasource.password=1234
```

Update the username and password according to your local MySQL installation.

---

## 7. JPA / Hibernate Configuration

The application uses Hibernate for ORM and JPA for persistence.

```properties
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.hibernate.ddl-auto=update
```

With:

```text
spring.jpa.hibernate.ddl-auto=update
```

Hibernate automatically creates or updates the required database tables based on the entity classes.

---

## 8. JWT Configuration

JWT configuration is defined in `application.properties`.

```properties
jwt.secret=YOUR_BASE64_SECRET
jwt.expiration=86400000
```

The expiration value:

```text
86400000 milliseconds = 24 hours
```

JWT tokens are generated after successful authentication and must be supplied to protected endpoints.

---

## 9. Running the Application

### Step 1 – Clone the repository

```bash
git clone <YOUR_GITHUB_REPOSITORY_URL>
```

### Step 2 – Navigate to the project

```bash
cd resource-booking-api
```

### Step 3 – Configure MySQL

Make sure MySQL is running and the database credentials in `application.properties` are correct.

### Step 4 – Build the project

```bash
mvn clean package
```

### Step 5 – Run the application

```bash
mvn spring-boot:run
```

The application runs on:

```text
http://localhost:8080
```

---

## 10. Seed Users

The application provides test users for ADMIN and USER roles.

### ADMIN

```text
Username: admin
Password: admin123
Role: ADMIN
```

### USER

```text
Username: user
Password: user123
Role: USER
```

These credentials are intended for local testing.

---

## 11. Authentication

### Login

```http
POST /auth/login
```

Example request:

```json
{
  "username": "admin",
  "password": "admin123"
}
```

A successful login returns a JWT token.

Example:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

Use the returned token for protected endpoints.

### Authorization Header

```http
Authorization: Bearer <JWT_TOKEN>
```

---

## 12. API Endpoints

### Authentication

| Method | Endpoint | Access |
|---|---|---|
| POST | `/auth/login` | Public |

---

## 13. Resource Endpoints

| Method | Endpoint | Access |
|---|---|---|
| GET | `/api/resources` | USER / ADMIN |
| GET | `/api/resources/{id}` | USER / ADMIN |
| POST | `/api/resources` | ADMIN |
| PUT | `/api/resources/{id}` | ADMIN |
| DELETE | `/api/resources/{id}` | ADMIN |

---

## 14. Reservation Endpoints

| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/reservations` | USER / ADMIN |
| GET | `/api/reservations` | USER / ADMIN |
| PATCH | `/api/reservations/{id}/status` | ADMIN |
| DELETE | `/api/reservations/{id}` | ADMIN |

---

## 15. Resource Management

### Create Resource

```http
POST /api/resources
```

Requires:

```text
ADMIN
```

Example request:

```json
{
  "name": "Conference Room A",
  "description": "Large conference room",
  "price": 1500.00
}
```

Successful response:

```text
201 Created
```

---

### Get All Resources

```http
GET /api/resources
```

Accessible by:

```text
USER
ADMIN
```

Example:

```http
GET /api/resources?page=0&size=10
```

---

### Get Resource By ID

```http
GET /api/resources/{id}
```

Example:

```http
GET /api/resources/1
```

---

### Update Resource

```http
PUT /api/resources/{id}
```

Requires:

```text
ADMIN
```

Example:

```json
{
  "name": "Conference Room Updated",
  "description": "Updated conference room",
  "price": 1800.00
}
```

---

### Delete Resource

```http
DELETE /api/resources/{id}
```

Requires:

```text
ADMIN
```

Successful response:

```text
204 No Content
```

---

## 16. Reservation Management

### Create Reservation

```http
POST /api/reservations
```

Accessible by:

```text
USER
ADMIN
```

Example request:

```json
{
  "resourceId": 1,
  "price": 1500.00,
  "status": "PENDING",
  "startTime": "2026-09-25T10:00:00",
  "endTime": "2026-09-25T12:00:00"
}
```

The user is obtained from the authenticated JWT.

The reservation request does **not** determine the authenticated user.

Successful response:

```text
201 Created
```

---

## 17. Reservation Status

Supported statuses:

```text
PENDING
CONFIRMED
CANCELLED
```

Example:

```json
{
  "status": "CONFIRMED"
}
```

---

## 18. Get Reservations

```http
GET /api/reservations
```

### USER

A USER receives only their own reservations.

### ADMIN

An ADMIN can view reservations belonging to all users.

---

## 19. Reservation Filtering

Reservations can be filtered using:

- `status`
- `minPrice`
- `maxPrice`

### Filter by Status

```http
GET /api/reservations?status=PENDING
```

### Minimum Price

```http
GET /api/reservations?minPrice=500
```

### Maximum Price

```http
GET /api/reservations?maxPrice=2000
```

### Combined Filters

```http
GET /api/reservations?status=CONFIRMED&minPrice=500&maxPrice=2000
```

---

## 20. Pagination

Reservation results support pagination using:

- `page`
- `size`

Example:

```http
GET /api/reservations?page=0&size=10
```

Where:

```text
page=0 → first page
size=10 → 10 records per page
```

---

## 21. Sorting

Reservation results support optional Spring Data sorting.

Example:

```http
GET /api/reservations?page=0&size=10&sort=price,asc
```

Another example:

```http
GET /api/reservations?page=0&size=10&sort=id,desc
```

---

## 22. Combined Reservation Query

Example:

```http
GET /api/reservations?status=CONFIRMED&minPrice=500&maxPrice=3000&page=0&size=10&sort=price,asc
```

This combines:

- Status filtering
- Minimum price
- Maximum price
- Pagination
- Sorting

---

## 23. Update Reservation Status

```http
PATCH /api/reservations/{id}/status
```

Requires:

```text
ADMIN
```

Example:

```http
PATCH /api/reservations/1/status?status=CONFIRMED
```

Possible statuses:

```text
PENDING
CONFIRMED
CANCELLED
```

Successful response:

```text
200 OK
```

---

## 24. Delete Reservation

```http
DELETE /api/reservations/{id}
```

Requires:

```text
ADMIN
```

Successful response:

```text
204 No Content
```

---

## 25. Role-Based Access Control

### ADMIN permissions

```text
Resource:
  GET       ✓
  POST      ✓
  PUT       ✓
  DELETE    ✓

Reservation:
  GET       ✓
  POST      ✓
  PATCH     ✓
  DELETE    ✓
```

### USER permissions

```text
Resource:
  GET       ✓
  POST      ✗
  PUT       ✗
  DELETE    ✗

Reservation:
  GET       ✓ (own reservations only)
  POST      ✓
  PATCH     ✗
  DELETE    ✗
```

---

## 26. Reservation Ownership

Reservation ownership is determined from the authenticated user.

The application uses the authenticated principal supplied by Spring Security:

```java
@AuthenticationPrincipal User currentUser
```

The reservation service associates the reservation with the authenticated user.

A USER cannot provide another user's ID in the reservation request to create a reservation on behalf of another user.

When retrieving reservations, USER results are filtered using the authenticated user's ID.

ADMIN users are allowed to view reservations for all users.

---

## 27. Validation

The application validates incoming requests using Jakarta Bean Validation.

Examples of validation include:

- Required fields
- Valid price values
- Valid resource ID
- Valid reservation status
- Valid start time
- Valid end time
- Start time must not be after end time

Invalid requests return:

```text
400 Bad Request
```

---

## 28. Error Handling

The application uses centralized exception handling through `GlobalExceptionHandler`.

### Common responses

#### 400 Bad Request

Used for invalid request data or validation errors.

Example:

```json
{
  "status": 400,
  "message": "price: must be greater than or equal to 0",
  "timestamp": "2026-09-30T10:00:00"
}
```

#### 401 Unauthorized

Returned when authentication is required but a valid JWT is not provided.

Example:

```json
{
  "status": 401,
  "message": "Authentication required"
}
```

#### 403 Forbidden

Returned when the authenticated user does not have sufficient privileges.

Example:

```json
{
  "status": 403,
  "message": "Access denied: insufficient privileges"
}
```

#### 404 Not Found

Returned when a requested resource or reservation does not exist.

Example:

```json
{
  "status": 404,
  "message": "Resource not found with id: 1"
}
```

---

## 29. Swagger / OpenAPI Documentation

The API is documented using Swagger/OpenAPI.

After starting the application, open:

```text
http://localhost:8080/swagger-ui.html
```

Alternatively:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI specification:

```text
http://localhost:8080/v3/api-docs
```

Swagger can be used to:

- View available endpoints
- View request/response schemas
- Test API endpoints
- Authenticate using JWT
- Test different role permissions

---

## 30. Using JWT in Swagger

1. Login using:

```text
POST /auth/login
```

2. Copy the returned JWT token.

3. Click the **Authorize** button in Swagger.

4. Enter:

```text
Bearer <your-jwt-token>
```

5. Click **Authorize**.

6. Protected endpoints can now be tested using the authenticated token.

---

## 31. Security Configuration

The application uses:

- Spring Security
- JWT authentication
- BCrypt password encoding
- Stateless session management
- Role-based endpoint authorization
- JWT request filtering
- Protected API endpoints

CSRF is disabled because the application uses stateless JWT authentication.

The security configuration permits access to:

```text
/auth/**
/swagger-ui/**
/v3/api-docs/**
/swagger-ui.html
```

Other application endpoints require authentication according to their configured roles.

---

## 32. Database Persistence

The application uses:

```text
Spring Data JPA
        ↓
Hibernate
        ↓
MySQL
```

Entities are mapped using JPA annotations.

Hibernate manages database table creation and updates using:

```properties
spring.jpa.hibernate.ddl-auto=update
```

---

## 33. Testing

The application was tested using Swagger/OpenAPI with both ADMIN and USER accounts.

### Authentication Testing

- Valid ADMIN login
- Valid USER login
- Invalid credentials
- Protected endpoint without JWT

### Authorization Testing

- ADMIN resource creation
- ADMIN resource update
- ADMIN resource deletion
- USER resource read access
- USER resource creation rejection
- USER resource update rejection
- USER resource deletion rejection

### Reservation Testing

- USER reservation creation
- ADMIN reservation creation
- USER own reservation retrieval
- ADMIN all reservation retrieval
- Reservation status update
- Reservation deletion
- Reservation ownership restrictions

### Validation Testing

- Missing required fields
- Negative price
- Invalid resource ID
- Invalid reservation times
- Invalid reservation data

### Filtering / Pagination / Sorting

- Status filtering
- Minimum price filtering
- Maximum price filtering
- Combined filters
- Pagination
- Sorting

---

## 34. HTTP Status Codes

| Status | Meaning |
|---|---|
| 200 | Successful request |
| 201 | Resource/reservation created |
| 204 | Successful deletion |
| 400 | Invalid request / validation error |
| 401 | Authentication required / invalid credentials |
| 403 | Insufficient permissions |
| 404 | Resource or reservation not found |
| 500 | Unexpected server error |

---

## 35. Configuration

Main configuration file:

```text
src/main/resources/application.properties
```

Example local configuration:

```properties
spring.application.name=resource-booking-api

spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/booking_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata
spring.datasource.username=root
spring.datasource.password=1234

spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.hibernate.ddl-auto=update

jwt.secret=YOUR_BASE64_SECRET
jwt.expiration=86400000

springdoc.swagger-ui.path=/swagger-ui.html
```

For production environments, database credentials and JWT secrets should be supplied using environment variables or a secure secrets-management mechanism instead of committing sensitive values to source control.

---

## 36. Build

Build the application using:

```bash
mvn clean package
```

Run the generated JAR:

```bash
java -jar target/resource-booking-api-0.0.1-SNAPSHOT.jar
```

The exact JAR filename may vary depending on the project version.

---

## 37. Example API Flow

A typical usage flow is:

```text
1. Start MySQL
       ↓
2. Start Spring Boot application
       ↓
3. Login using /auth/login
       ↓
4. Receive JWT token
       ↓
5. Authorize using JWT
       ↓
6. Access protected resources
       ↓
7. Create reservations
       ↓
8. Retrieve reservations
       ↓
9. Apply filters/pagination/sorting
       ↓
10. ADMIN manages resources and reservation status
```

---

## 38. Security Notes

- Passwords are stored using BCrypt hashing.
- JWT is used for stateless authentication.
- Protected endpoints require authentication.
- USER access is restricted according to role.
- USER reservation ownership is determined from the authenticated JWT user.
- ADMIN has broader access to resources and reservations.
- Sensitive production credentials should not be committed to source control.

---

## 39. Assignment Requirements Coverage

| Requirement | Implementation |
|---|---|
| JWT login | `POST /auth/login` |
| ADMIN / USER roles | Spring Security RBAC |
| ADMIN resource CRUD | Resource controller/service |
| USER resource read-only | Security configuration |
| USER reservation creation | Reservation API |
| USER own reservations | User-based filtering |
| JWT-based user identity | `@AuthenticationPrincipal` |
| Reservation statuses | PENDING, CONFIRMED, CANCELLED |
| Decimal price | `BigDecimal` |
| Status filtering | Reservation query |
| Minimum price filtering | Reservation query |
| Maximum price filtering | Reservation query |
| Pagination | Spring `Pageable` |
| Sorting | Spring `Pageable` / `Sort` |
| ADMIN all reservations | Role-based service filtering |
| Validation | Jakarta Bean Validation |
| Error handling | Global exception handler |
| MySQL | Spring Data JPA / Hibernate |
| API documentation | Swagger / OpenAPI |
| Seed users | ADMIN and USER test accounts |
| Setup documentation | README |

---

## 40. Author

Developed as part of the **EXELYNT Backend Developer Assignment**.

**Project:** Resource Booking API

**Backend:** Spring Boot / Java

**Database:** MySQL

**Authentication:** JWT

**Authorization:** Spring Security RBAC

---