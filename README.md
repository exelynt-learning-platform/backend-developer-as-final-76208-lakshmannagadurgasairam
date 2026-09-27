# Resource Booking API

Backend Developer Assignment for Exelynt.

A secure RESTful Resource Booking API built using Spring Boot, Java 17, Spring Security, JWT authentication, MySQL, and Spring Data JPA.

---

## 1. Project Overview

The Resource Booking API allows authenticated users to view resources and create reservations.

The application supports two roles:

* `ADMIN`
* `USER`

### ADMIN

ADMIN users can:

* Create resources
* View resources
* Update resources
* Delete resources
* View all reservations
* Create reservations
* Update reservation status
* Delete reservations

### USER

USER users can:

* View resources
* Create reservations
* View only their own reservations

USER users cannot:

* Create, update, or delete resources
* Update reservation status
* Delete reservations
* Access other users' reservations

---

## 2. Technology Stack

* Java 17+
* Spring Boot 3.2.3
* Spring Security
* JWT
* Spring Data JPA
* Hibernate
* MySQL
* Maven
* Swagger / OpenAPI
* Lombok
* Jakarta Bean Validation

---

## 3. Project Structure

```text
src/
├── main/
│   ├── java/
│   │   └── com/example/booking/
│   │       ├── config/
│   │       ├── controller/
│   │       ├── dto/
│   │       ├── entity/
│   │       ├── exception/
│   │       ├── repository/
│   │       ├── security/
│   │       ├── service/
│   │       └── validation/
│   │
│   └── resources/
│       └── application.properties/
│
└── test/
    └── java/
```

---

## 4. Main Features

* JWT-based authentication
* Role-based access control
* ADMIN and USER roles
* Resource CRUD operations
* Reservation management
* Reservation ownership
* Reservation status management
* Price filtering
* Status filtering
* Pagination
* Sorting
* Request validation
* Centralized exception handling
* MySQL persistence
* Swagger/OpenAPI documentation
* Unit/service testing

---

## 5. Prerequisites

Install the following before running the application:

* Java 17 or later
* MySQL 8+
* Maven or Maven Wrapper
* Git

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

Default database:

```text
booking_db
```

The application can also create the database automatically because the JDBC URL contains:

```text
createDatabaseIfNotExist=true
```

### Database Environment Variables

Database credentials are not stored directly in `application.properties`.

The application expects the following environment variables:

```text
DB_USERNAME
DB_PASSWORD
```

### Windows Command Prompt

```cmd
set DB_USERNAME=root
set DB_PASSWORD=your_mysql_password
```

### Windows PowerShell

```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_mysql_password"
```

The application uses:

```properties
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/booking_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

Update the environment variables according to your local MySQL installation.

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

The JWT secret is supplied through the `JWT_SECRET` environment variable and is not committed to source control.

```properties
jwt.secret=${JWT_SECRET}
jwt.expiration=86400000
```

The expiration value:

```text
86400000 milliseconds = 24 hours
```

### Windows Command Prompt

```cmd
set JWT_SECRET=your_generated_base64_secret
```

### Windows PowerShell

```powershell
$env:JWT_SECRET="your_generated_base64_secret"
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

Make sure MySQL is running.

Configure the following environment variables:

```text
DB_USERNAME
DB_PASSWORD
JWT_SECRET
```

### Windows Command Prompt

```cmd
set DB_USERNAME=root
set DB_PASSWORD=your_mysql_password
set JWT_SECRET=your_generated_base64_secret
```

### Windows PowerShell

```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_mysql_password"
$env:JWT_SECRET="your_generated_base64_secret"
```

### Step 4 – Build the project

Using Maven:

```bash
mvn clean package
```

Or using the Maven Wrapper on Windows:

```cmd
mvnw.cmd clean package
```

### Step 5 – Run the application

Using Maven:

```bash
mvn spring-boot:run
```

Or using the Maven Wrapper on Windows:

```cmd
mvnw.cmd spring-boot:run
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

| Method | Endpoint      | Access |
| ------ | ------------- | ------ |
| POST   | `/auth/login` | Public |

---

## 13. Resource Endpoints

| Method | Endpoint              | Access       |
| ------ | --------------------- | ------------ |
| GET    | `/api/resources`      | USER / ADMIN |
| GET    | `/api/resources/{id}` | USER / ADMIN |
| POST   | `/api/resources`      | ADMIN        |
| PUT    | `/api/resources/{id}` | ADMIN        |
| DELETE | `/api/resources/{id}` | ADMIN        |

---

## 14. Reservation Endpoints

| Method | Endpoint                        | Access       |
| ------ | ------------------------------- | ------------ |
| POST   | `/api/reservations`             | USER / ADMIN |
| GET    | `/api/reservations`             | USER / ADMIN |
| PATCH  | `/api/reservations/{id}/status` | ADMIN        |
| DELETE | `/api/reservations/{id}`        | ADMIN        |

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

Supported sorting fields:

```text
id
name
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
  "startTime": "2026-09-25T10:00:00",
  "endTime": "2026-09-25T12:00:00"
}
```

The authenticated user is obtained from the JWT.

The reservation request does not determine the authenticated user.

New reservations are created with:

```text
PENDING
```

The reservation status supplied by a client is not used to create a new reservation.

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

New reservations always start with:

```text
PENDING
```

Only ADMIN users can update reservation status.

Example:

```http
PATCH /api/reservations/1/status?status=CONFIRMED
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

* `status`
* `minPrice`
* `maxPrice`

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

If `minPrice` is greater than `maxPrice`, the API returns:

```text
400 Bad Request
```

---

## 20. Pagination

Reservation results support pagination using:

* `page`
* `size`

Example:

```http
GET /api/reservations?page=0&size=10
```

Where:

```text
page=0 → first page
size=10 → 10 records per page
```

The API accepts:

```text
page >= 0
size between 1 and 100
```

Invalid pagination values return:

```text
400 Bad Request
```

---

## 21. Sorting

Reservation results support optional sorting.

Allowed reservation sort fields are:

```text
id
price
status
startTime
endTime
```

Example:

```http
GET /api/reservations?page=0&size=10&sort=price,asc
```

Another example:

```http
GET /api/reservations?page=0&size=10&sort=id,desc
```

Unsupported sort fields return:

```text
400 Bad Request
```

---

## 22. Combined Reservation Query

Example:

```http
GET /api/reservations?status=CONFIRMED&minPrice=500&maxPrice=3000&page=0&size=10&sort=price,asc
```

This combines:

* Status filtering
* Minimum price
* Maximum price
* Pagination
* Sorting

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

A USER attempting this operation receives:

```text
403 Forbidden
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

* Required fields
* Valid price values
* Valid resource ID
* Valid reservation status
* Valid start time
* Valid end time
* Start time must not be after end time
* Valid pagination values
* Valid sorting fields

Invalid requests return:

```text
400 Bad Request
```

---

## 28. Error Handling

The application uses centralized exception handling through `GlobalExceptionHandler`.

Unexpected server-side exceptions are logged internally and return a generic error message to the client.

### Common responses

#### 400 Bad Request

Used for invalid request data, validation errors, invalid pagination, invalid sorting, or invalid filters.

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

#### 500 Internal Server Error

Unexpected application errors return a generic message:

```json
{
  "status": 500,
  "message": "An unexpected error occurred",
  "timestamp": "2026-09-30T10:00:00"
}
```

Detailed exception information is logged internally and is not exposed to API clients.

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

* View available endpoints
* View request/response schemas
* Test API endpoints
* Authenticate using JWT
* Test different role permissions

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

* Spring Security
* JWT authentication
* BCrypt password encoding
* Stateless session management
* Role-based endpoint authorization
* JWT request filtering
* Protected API endpoints

CSRF is disabled because the application uses stateless JWT authentication.

The security configuration permits access to:

```text
/auth/**
/swagger-ui/**
/v3/api-docs/**
/swagger-ui.html
```

Resource permissions are explicitly configured by HTTP method:

```text
GET     → USER / ADMIN
POST    → ADMIN
PUT     → ADMIN
DELETE  → ADMIN
```

Reservation permissions are:

```text
GET     → USER / ADMIN
POST    → USER / ADMIN
PATCH   → ADMIN
DELETE  → ADMIN
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

* Valid ADMIN login
* Valid USER login
* Invalid credentials
* Protected endpoint without JWT

### Authorization Testing

* ADMIN resource creation
* ADMIN resource update
* ADMIN resource deletion
* USER resource read access
* USER resource creation rejection
* USER resource update rejection
* USER resource deletion rejection

### Reservation Testing

* USER reservation creation
* ADMIN reservation creation
* USER own reservation retrieval
* ADMIN all reservation retrieval
* Reservation status update
* USER reservation status update rejection
* Reservation deletion
* Reservation ownership restrictions
* New reservations created with PENDING status

### Validation Testing

* Missing required fields
* Negative price
* Invalid resource ID
* Invalid reservation times
* Invalid reservation data
* Invalid pagination
* Invalid sorting
* Invalid price range

### Filtering / Pagination / Sorting

* Status filtering
* Minimum price filtering
* Maximum price filtering
* Combined filters
* Pagination
* Sorting

---

## 34. HTTP Status Codes

| Status | Meaning                                       |
| ------ | --------------------------------------------- |
| 200    | Successful request                            |
| 201    | Resource/reservation created                  |
| 204    | Successful deletion                           |
| 400    | Invalid request / validation error            |
| 401    | Authentication required / invalid credentials |
| 403    | Insufficient permissions                      |
| 404    | Resource or reservation not found             |
| 500    | Unexpected server error                       |

---

## 35. Configuration

Main configuration file:

```text
src/main/resources/application.properties
```

The committed configuration uses environment variables for sensitive values:

```properties
spring.application.name=resource-booking-api

spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/booking_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.hibernate.ddl-auto=update

jwt.secret=${JWT_SECRET}
jwt.expiration=86400000

springdoc.swagger-ui.path=/swagger-ui.html
```

Required environment variables:

```text
DB_USERNAME
DB_PASSWORD
JWT_SECRET
```

Sensitive database credentials and JWT secrets should not be committed to source control.

For production environments, secrets should be supplied through environment variables or a secure secrets-management mechanism.

---

## 36. Build

Build the application using:

```bash
mvn clean package
```

On Windows using the Maven Wrapper:

```cmd
mvnw.cmd clean package
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
2. Configure DB_USERNAME, DB_PASSWORD and JWT_SECRET
       ↓
3. Start Spring Boot application
       ↓
4. Login using /auth/login
       ↓
5. Receive JWT token
       ↓
6. Authorize using JWT
       ↓
7. Access protected resources
       ↓
8. Create reservations
       ↓
9. Retrieve reservations
       ↓
10. Apply filters/pagination/sorting
       ↓
11. ADMIN manages resources and reservation status
```

---

## 38. Security Notes

* Passwords are stored using BCrypt hashing.
* JWT is used for stateless authentication.
* Protected endpoints require authentication.
* USER access is restricted according to role.
* USER reservation ownership is determined from the authenticated JWT user.
* New reservations are created with `PENDING` status.
* Only ADMIN users can update reservation status.
* ADMIN has broader access to resources and reservations.
* Database credentials are supplied through environment variables.
* JWT secrets are supplied through environment variables.
* Sensitive credentials and secrets should not be committed to source control.
* Unexpected server-side exception details are logged internally and are not exposed to API clients.

---

## 39. Assignment Requirements Coverage

| Requirement                       | Implementation                                 |
| --------------------------------- | ---------------------------------------------- |
| JWT login                         | `POST /auth/login`                             |
| ADMIN / USER roles                | Spring Security RBAC                           |
| ADMIN resource CRUD               | Resource controller/service                    |
| USER resource read-only           | Security configuration                         |
| USER reservation creation         | Reservation API                                |
| USER own reservations             | User-based filtering                           |
| JWT-based user identity           | `@AuthenticationPrincipal`                     |
| Reservation statuses              | PENDING, CONFIRMED, CANCELLED                  |
| Decimal price                     | `BigDecimal`                                   |
| Status filtering                  | Reservation query                              |
| Minimum price filtering           | Reservation query                              |
| Maximum price filtering           | Reservation query                              |
| Pagination                        | Spring `Pageable`                              |
| Sorting                           | Spring `Pageable` / `Sort` with allowed fields |
| ADMIN all reservations            | Role-based service filtering                   |
| Validation                        | Jakarta Bean Validation                        |
| Error handling                    | Global exception handler                       |
| Generic unexpected error response | Centralized logging and generic 500 response   |
| MySQL                             | Spring Data JPA / Hibernate                    |
| API documentation                 | Swagger / OpenAPI                              |
| Seed users                        | ADMIN and USER test accounts                   |
| Environment-based secrets         | DB credentials and JWT secret                  |
| Setup documentation               | README                                         |

---

## 40. Author

Developed as part of the **EXELYNT Backend Developer Assignment**.

**Project:** Resource Booking API

**Backend:** Spring Boot / Java

**Database:** MySQL

**Authentication:** JWT

**Authorization:** Spring Security RBAC
