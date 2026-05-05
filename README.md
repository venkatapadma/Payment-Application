💳 Payment Application – Microservices Architecture

A production-ready payment application built using Java 21 and Spring Boot 4.0.0, following microservices architecture principles. 
The system supports secure payments, wallet management, rewards processing, and real-time notifications, all orchestrated through an API Gateway and 
implemented with event-driven communication using Kafka.


🏗️ Architecture Overview

The application is divided into independent, loosely coupled microservices, each responsible for a specific business capability. 
Services communicate via REST APIs (Feign Client) and Apache Kafka events, coordinated using the Saga Design Pattern to ensure data consistency across distributed transactions.


🔧 Microservices Description


👤 User Service

User registration and authentication
JWT token generation and validation
Role-based access control


💰 Wallet Service

Wallet creation and balance management
Credit and debit operations
Participates in Saga transactions


🔄 Transaction Service

Payment processing and transaction lifecycle
Publishes transaction events to Kafka
Ensures consistency using Saga pattern


🎁 Reward Service

Reward/cashback calculation
Listens to transaction success events
Updates reward points asynchronously

🔔 Notification Service

Sends transaction and reward notifications
Consumes Kafka events for real-time alerts


🌐 API Gateway

Single entry point for all client requests
Centralized authentication and authorization
Rate limiting to prevent abuse
Request routing and validation


🔐 Security Features

Spring Security for authentication & authorization
JWT (JSON Web Tokens) for stateless session management
Role-based access control (RBAC)
Rate Limiting at API Gateway level


🔁 Saga Design Pattern

Choreography-based Saga
Ensures eventual consistency across microservices
Kafka used as the event backbone for success/failure events
Compensating actions triggered on failure


📚 API Documentation

Integrated Swagger / OpenAPI
Available for each microservice
Easy API testing and documentation

http://localhost:8080/swagger-ui.html


🧪 Database & Persistence

Service Type  Database
Local / Dev  H2 Database
Production  MySQL


JPA & Hibernate for persistence
Database per service pattern followed


🚀 Technology Stack (Highlights)
⚙️ Core Technologies

Java 21
Spring Boot 4.0.0
Maven


🧩 Microservices & Communication

Spring Cloud OpenFeign
Spring Cloud API Gateway
Apache Kafka


🔐 Security

Spring Security
JWT Authentication


📊 Data & Persistence

MySQL
H2 Database
Spring Data JPA


📖 Documentation & Tooling

Swagger / OpenAPI
Lombok


✅ Key Features

✅ Microservices-based scalable architecture

✅ Event-driven communication with Kafka

✅ Distributed transaction handling using Saga Pattern

✅ Secure APIs with JWT & Spring Security

✅ Centralized API Gateway with Rate Limiting

✅ API documentation using Swagger

✅ Java 21 & Spring Boot 4 optimized
