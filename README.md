# 🏘️ NayaBas.com.np (नयाँ बस)

NayaBas.com.np ("New Dwelling/Stay") is a full-stack web application designed to simplify the process of finding, listing, and renting houses or flats in Nepal. The platform provides a centralized, user-friendly, and secure environment to connect renters (Customers) and property owners/agents (Sellers).

## 💡 Project Goal

The project solves the inefficiency and fragmentation of traditional rental processes in Nepal by offering enhanced search, streamlined communication (Real-time Chat), and secure, role-based management for all user interactions.

## ✨ Key Features

The application is built around three core user roles—**Customer (Renter)**, **Seller (Owner/Agent)**, and **Admin**—each with tailored permissions.

| Feature Category | Description | Key Implementations |
| :--- | :--- | :--- |
| **Property Management** | Listing, searching, and filtering rental properties. | Advanced Mapping, Review & Rating System. |
| **Interaction Flow** | Status-driven process for formalizing interest. | Booking and Meeting request management. |
| **Communication** | Direct and immediate contact between users. | Real-time Chat using WebSockets, Notifications (Email/SMS). |
| **Administration** | Full oversight and control of system resources. | Dedicated Admin Dashboard. |

## 💻 Technology Stack

This project follows a modern, scalable, and secure full-stack architecture.

| Layer | Technology | Rationale |
| :--- | :--- | :--- |
| **Frontend** | **Angular** | Powerful framework for building dynamic Single-Page Applications (SPAs). |
| **Backend** | **Spring Boot (Java)** | Robust and scalable framework for high-performance, secure RESTful APIs. |
| **Database** | **MySQL** | Reliable and widely-used relational database for structured data persistence. |
| **Security** | **Spring Security, JWT** | Implements JWT Authentication and Role-Based Access Control (RBAC). |

## 🏗️ Application Architecture

The system uses a classic client-server model with a layered backend structure to ensure maintainability and separation of concerns:

1.  **Controller Layer:** Handles incoming HTTP requests and API endpoints.
2.  **Service Layer:** Contains core business logic and transaction management.
3.  **Repository Layer:** Manages data persistence and database queries (JPA/Hibernate).
4.  **Security Layer:** Manages authentication and authorization checks.

## 🔌 API Structure (Key Endpoints)

API access is secured using JWTs and controlled via Role-Based Access Control (RBAC).

| Category | Example Endpoint | Method | Role |
| :--- | :--- | :--- | :--- |
| **Authentication** | `/api/auth/register` | `POST` | Public |
| **Properties** | `/api/properties` | `GET` | Public |
| **Property Mgt.** | `/api/properties/{id}` | `PUT`/`DELETE` | Owner/Admin |
| **Bookings** | `/api/bookings` | `POST` | Customer |
| **Meetings** | `/api/meetings/{id}/status` | `PUT` | Seller |

## 🛡️ Security Implementation

* **JWT Authentication:** Secure tokens are issued upon login and used for subsequent API request authentication.
* **Role-Based Access Control (RBAC):** Strict permissions based on the **Admin**, **Customer**, and **Seller** roles.
* **Ownership Checks:** Ensures users can only perform actions (e.g., updating property details) on resources they own.

## 📈 Future Enhancements

[cite_start]The project is designed with scalability in mind. Potential additions for the future include:

* **Payment Gateway:** Integration for secure online payments (e.g., booking deposits).
* **AI Chatbot:** An intelligent assistant for user support and navigation.
* **User Wallet System:** A secure in-app wallet for managing booking payments and refunds.
* **Dynamic Pricing:** Algorithms to suggest optimal rental prices based on market data.
* **Enhanced Security:** Implementation of features like Two-Factor Authentication (2FA).

## 🚀 Getting Started

Follow these instructions to set up and run the project locally.

### Prerequisites

* JDK 17 or higher
* Node.js (for Angular)
* MySQL Server
* Maven or Gradle (for Spring Boot)

### 1. Backend Setup

1.  Navigate to the `nayabas-backend` directory.
    ```bash
    cd nayabas-backend
    ```
2.  Configure your database connection properties (username, password, database name) in:
    `src/main/resources/application.properties`
3.  Build and run the Spring Boot application:
    ```bash
    # Using Maven
    ./mvnw spring-boot:run
    ```

### 2. Frontend Setup

1.  Navigate to the `nayabas-angular` directory.
    ```bash
    cd nayabas-angular
    ```
2.  Install all project dependencies:
    ```bash
    npm install
    ```
3.  Run the Angular development server:
    ```bash
    ng serve
    ```

The application will be accessible at `http://localhost:4200/`.

---
