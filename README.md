# Negotiated Rates API - Modernized Implementation

## Overview

This project is a modernization of the ABC Bank Negotiated Rates application.

The original implementation has been upgraded to a modern enterprise architecture using:

* Spring Boot
* PostgreSQL
* Keycloak Authentication & Authorization
* JWT Security
* Role-Based Access Control (RBAC)
* RabbitMQ Messaging
* Dockerized Infrastructure
* Swagger/OpenAPI Documentation

The application allows customers to request negotiated foreign exchange rates and treasury officers to approve or reject those requests.

---

# Local Development Setup

This section describes how to set up and run the Negotiated Rates application on a new machine.

---

# Prerequisites

Install the following software:

| Software       | Version     |
| -------------- | ----------- |
| Java           | 11 or later |
| Maven          | 3.8+        |
| PostgreSQL     | 14+         |
| Docker         | Latest      |
| Docker Compose | Latest      |
| Keycloak       | 26.x        |
| RabbitMQ       | Latest      |
| Git            | Latest      |

Verify installation:

```bash
java -version
mvn -version
docker --version
docker compose version
git --version
```

---

# Clone Repository

```bash
git clone https://github.com/egarajere-png/revisedNegotiatedRates
cd revisedNegotiatedRates
```

---

# PostgreSQL Setup

Create database:

```psql
CREATE DATABASE abconnect;
```

Create user:

```sql
CREATE USER testuser WITH PASSWORD 'password';
```

Grant privileges:

```sql
GRANT ALL PRIVILEGES ON DATABASE abconnect TO testuser;
```

Verify connection:

```bash
psql -U testuser -d abconnect
```

---

# RabbitMQ Setup

## Option 1: Docker (Recommended)

```bash
docker run -d \
--name rabbitmq \
-p 5672:5672 \
-p 15672:15672 \
rabbitmq:3-management
```

Management Console:

```text
http://localhost:15672
```

Default credentials:

```text
Username: guest
Password: guest
```

Verify:

```bash
docker ps
```

---

## Option 2: Native Installation

Ubuntu:

```bash
sudo apt update
sudo apt install rabbitmq-server
```

Start service:

```bash
sudo systemctl start rabbitmq-server
sudo systemctl enable rabbitmq-server
```

Check status:

```bash
sudo systemctl status rabbitmq-server
```

---

# Keycloak Setup

## Start Keycloak

Using Docker:

```bash
docker run -d \
--name keycloak \
-p 8080:8080 \
-e KEYCLOAK_ADMIN=admin \
-e KEYCLOAK_ADMIN_PASSWORD=admin \
quay.io/keycloak/keycloak:latest \
start-dev
```

Access:

```text
http://localhost:8080
```

Admin credentials:

```text
Username: admin
Password: admin
```

---

# Create Realm

Navigate to:

```text
Keycloak Admin Console
```

Create realm:

```text
negotiated-rates
```

---

# Create Client

Navigate:

```text
Realm
  -> Clients
  -> Create Client
```

Client ID:

```text
negotiated-rates-api
```

Settings:

```text
Client Authentication = ON
Authorization = OFF
Standard Flow = ON
Direct Access Grants = ON
Service Accounts = OFF
```

Save.

---

# Obtain Client Secret

Navigate:

```text
Clients
  -> negotiated-rates-api
  -> Credentials
```

Copy:

```text
Client Secret
```

This value will be used in configurations(application-dev.properties):

```properties
params.keycloak.config.client-secret=<CLIENT_SECRET>
```

---

# Create Realm Roles

Navigate:

```text
Realm Roles
```

Create:

```text
CUSTOMER
TREASURER
ADMIN
```

---

# Create Users

## Customer User

Navigate:

```text
Users
 -> Create User
```

Example:

```text
Username: customer
Email: customer@gmail.com
Enabled: ON
```

Set password:

```text
password123
```

Disable:

```text
Temporary Password
```

Assign role:

```text
CUSTOMER
```

---

## Treasurer User

Example:

```text
Username: treasurer
```

Assign role:

```text
TREASURER
```

---

## Admin User

Example:

```text
Username: adminuser
```

Assign role:

```text
ADMIN
```

---

# Application Configuration

Create or update:

```text
src/main/resources/application-dev.properties
```

Configuration:

```properties
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.generate-ddl=true

spring.datasource.username=abconnect
spring.datasource.password=abconnect
spring.datasource.url=jdbc:postgresql://localhost:5433/abconnect

params.keycloak.config.token-url=http://localhost:8080/realms/negotiated-rates/protocol/openid-connect/token
params.keycloak.config.clientid=negotiated-rates-api
params.keycloak.config.client-secret=<CLIENT_SECRET>

spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8080/realms/negotiated-rates

spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
```

---

# Build Project

Clean and build:

```bash
mvn clean install
```

Expected:

```text
BUILD SUCCESS
```

---

# Run Application

Using Maven:

```bash
mvn spring-boot:run
```

Or:

```bash
java -jar target/*.jar
```

Application should start on:

```text
http://localhost:8090
```

---

# Verify Keycloak Connectivity

Generate token:

```bash
curl -X POST \
http://localhost:8080/realms/negotiated-rates/protocol/openid-connect/token \
-H "Content-Type: application/x-www-form-urlencoded" \
-d "grant_type=password" \
-d "client_id=negotiated-rates-api" \
-d "client_secret=<CLIENT_SECRET>" \
-d "username=egarajere" \
-d "password=password123"
```

Expected:

```json
{
  "access_token": "...",
  "expires_in": 1800
}
```

---

# Swagger UI

Swagger is enabled for API exploration and testing.

Access:

```text
http://localhost:8090/swagger-ui/index.html
```

OpenAPI specification:

```text
http://localhost:8090/v3/api-docs
```

---

# Swagger Authorization

Step 1:

Generate token using:

```http
POST /negotiated-rates/api/auth-token
```

Request body:

```json
{
  "username": "egarajere",
  "password": "password123"
}
```

Step 2:

Copy:

```text
access_token
```

Step 3:

Click:

```text
Authorize
```

Step 4:

Enter:

```text
Bearer <ACCESS_TOKEN>
```

Step 5:

Execute protected endpoints.

---

# Security Validation

Verify CUSTOMER access:

```http
GET /negotiated-rates/test-customer
```

Expected:

```text
Customer access granted
```

Verify TREASURER access:

```http
GET /negotiated-rates/test-treasurer
```

Expected:

```text
Treasurer access granted
```

Verify unauthorized access:

```http
GET /negotiated-rates/test-treasurer
```

using CUSTOMER token.

Expected:

```http
403 Forbidden
```

This confirms role-based authorization is functioning correctly.




# Technology Stack

## Backend

* Java 11+
* Spring Boot
* Spring Security
* Spring Data JPA
* Hibernate

## Database

* PostgreSQL

## Authentication & Authorization

* Keycloak
* OAuth2 Resource Server
* JWT Bearer Tokens
* Role-Based Access Control

## Messaging

* RabbitMQ

## API Documentation

* Swagger UI
* OpenAPI 3

## Containerization

* Docker
* Docker Compose

---

# Architecture

```text
Customer/Treasurer
        |
        |
        V
   Spring Boot API
        |
        |
        +----------------+
        |                |
        V                V
 PostgreSQL         RabbitMQ
        |
        V
    Keycloak
```

---

# Security Implementation

## Authentication

Authentication is handled using Keycloak.

The application acts as an OAuth2 Resource Server.

Users authenticate against Keycloak and receive JWT access tokens.

Example:

```bash
curl -X POST \
http://localhost:8080/realms/negotiated-rates/protocol/openid-connect/token \
-H "Content-Type: application/x-www-form-urlencoded" \
-d "grant_type=password" \
-d "client_id=negotiated-rates-api" \
-d "client_secret=<CLIENT_SECRET>" \
-d "username=<USERNAME>" \
-d "password=<PASSWORD>"
```

---

## Authorization

Role-based authorization is enforced using:

```java
@PreAuthorize("hasRole('CUSTOMER')")
@PreAuthorize("hasRole('TREASURER')")
@PreAuthorize("hasRole('ADMIN')")
```

Roles are extracted directly from Keycloak JWT tokens.

---

## JWT Role Mapping

A custom converter was implemented:

```java
KeycloakJwtRoleConverter
```

Responsibilities:

* Extract realm roles from JWT
* Convert Keycloak roles into Spring authorities
* Prefix roles with ROLE_

Example:

```text
CUSTOMER
TREASURER
ADMIN
```

becomes:

```text
ROLE_CUSTOMER
ROLE_TREASURER
ROLE_ADMIN
```

---

# Security Configuration

## SecurityConfig

Features implemented:

* OAuth2 Resource Server
* JWT validation
* Role mapping
* Swagger public access
* Auth token endpoint public access
* Authentication required for all other endpoints

Public endpoints:

```text
/swagger-ui/**
/swagger-ui.html
/v3/api-docs/**
/negotiated-rates/api/auth-token
```

Protected endpoints:

```text
All remaining endpoints
```

---

# Keycloak Configuration

## Realm

```text
negotiated-rates
```

## Client

```text
negotiated-rates-api
```

Client Type:

```text
Confidential
```

Client Authentication:

```text
Enabled
```

---

## Realm Roles

```text
CUSTOMER
TREASURER
ADMIN
```

---

## Test Users

### Customer

```text
Username: customer1
Role: CUSTOMER
```

### Treasurer

```text
Username: treasurer
Role: TREASURER
```

---

# Auth Token Endpoint

Custom endpoint:

```http
POST /negotiated-rates/api/auth-token
```

Purpose:

* Accept username/password
* Forward request to Keycloak
* Return JWT token response

Request:

```json
{
  "username": "customer1",
  "password": "password"
}
```

---

## Important Fix Implemented

### Problem

The application returned:

```json
{
  "access_token": null
}
```

and logs showed:

```text
401 Unauthorized
```

### Root Cause

The Keycloak client was configured as:

```text
Confidential Client
```

but the application was not sending:

```text
client_secret
```

during authentication.

### Solution

Added:

```java
map.add("client_secret", keyCloakClientSecret);
```

to AuthController.

Final implementation:

```java
map.add("client_id", keyCloakClientId);
map.add("client_secret", keyCloakClientSecret);
map.add("username", authPayload.getUsername());
map.add("password", authPayload.getPassword());
map.add("grant_type", "password");
```

Result:

```text
Authentication successful
JWT token returned
```

---

# Application Properties

## application-dev.properties

```properties
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update

spring.datasource.username=abconnect
spring.datasource.password=abconnect
spring.datasource.url=jdbc:postgresql://localhost:5433/abconnect

params.keycloak.config.token-url=http://localhost:8080/realms/negotiated-rates/protocol/openid-connect/token
params.keycloak.config.clientid=negotiated-rates-api
params.keycloak.config.client-secret=<CLIENT_SECRET>

spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8080/realms/negotiated-rates

spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
```

---

# Endpoint Security Verification

## Treasurer Endpoint

Endpoint:

```http
GET /negotiated-rates/test-treasurer
```

Expected:

```text
Treasurer access granted
```

Verified:

✅ Success

---

## Customer Endpoint

Endpoint:

```http
GET /negotiated-rates/test-customer
```

Expected:

```text
Customer access granted
```

Verified:

✅ Success

---

## Unauthorized Treasurer Access

Customer token used against:

```http
GET /negotiated-rates/test-treasurer
```

Result:

```http
403 Forbidden
```

Verified:

✅ Success

This confirms RBAC is functioning correctly.

---

# RabbitMQ Integration

RabbitMQ is used for:

* Event messaging
* Notification processing
* Future asynchronous workflows

Configuration:

```properties
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
```

---

# Database

Database Engine:

```text
PostgreSQL
```

Connection:

```properties
jdbc:postgresql://localhost:5433/abconnect
```

Hibernate Strategy:

```properties
spring.jpa.hibernate.ddl-auto=update
```

---

# Swagger Testing

Swagger URL:

```text
http://localhost:8090/swagger-ui/index.html
```

Workflow:

1. Generate token using auth endpoint
2. Copy access token
3. Click Authorize
4. Enter:

Bearer <ACCESS_TOKEN>

5. Execute protected endpoints

---

# Current Status

Completed:

* Spring Boot migration
* PostgreSQL integration
* Docker environment
* Keycloak integration
* JWT authentication
* Role mapping
* RBAC implementation
* Swagger integration
* RabbitMQ integration
* Customer authorization testing
* Treasurer authorization testing
* Token generation testing
* Endpoint protection testing

---

# Future Enhancements

* Refresh token support
* Keycloak user self-registration
* Audit logging
* Distributed tracing
* API rate limiting
* Prometheus metrics
* Grafana dashboards
* CI/CD pipeline
* Kubernetes deployment
* Centralized logging (ELK Stack)

---

# Author

Egara Jere

Modernization and Security Enhancement of the ABC Bank Negotiated Rates Platform.
