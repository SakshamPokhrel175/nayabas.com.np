-----

# NayaBas.com.np (नयाँ बस - "New Dwelling/Stay")

### A Full-Stack Rental Property Platform for Nepal

## 🏠 Project Overview

NayaBas.com.np is a full-stack web application designed to modernize the process of finding and renting houses and flats in Nepal. The platform acts as a centralized hub, connecting property owners and agents with prospective renters, and streamlines the entire process from property listings to secure booking and meeting arrangements.

## ✨ Key Features

The application is built with a robust, multi-role user system that includes tailored functionalities for each user type.

  * **Customer (Renter):**

      * Can securely register and log in to the platform.
      * Can search and filter properties based on criteria like location, price, and amenities.
      * Can view detailed property information, including images and availability.
      * Can request meetings or viewings with property owners and submit booking requests.

  * **Seller (Property Owner/Agent):**

      * Can securely register and list new properties with comprehensive details.
      * Can manage and update their existing property listings.
      * Can review and respond to all incoming booking and meeting requests from customers.

  * **Admin:**

      * Has full administrative control over all users, properties, and system settings.

## 💻 Technology Stack

The project is built on a modern and scalable full-stack architecture to ensure high performance and maintainability.

  * **Frontend:**
      * **Framework:** Angular 
      * **UI/Styling:** Angular Material, SCSS 
  * **Backend:**
      * **Framework:** Spring Boot (Java 17) 
      * **Security:** Spring Security, JWT (JSON Web Tokens) 
  * **Database:**
      * **Type:** MySQL 
      * **ORM:** Spring Data JPA 
  * **Build Tool:** Maven 

## 🏗️ High-Level Architecture

The system utilizes a standard client-server architecture with a layered backend to ensure a clear separation of concerns and maintainability.

```
+-------------------+      +-------------------------+      +-------------------+
|  Angular Frontend | <--> |   Spring Boot Backend   | <--> |  MySQL Database   |
|   (User Interface)|      |  (REST APIs & Logic)    |      | (Data Storage)    |
+-------------------+      +-------------------------+      +-------------------+
```

  * **Angular Frontend:** Serves as the user interface and interacts with the backend via HTTP requests.
  * **Spring Boot Backend:** Processes requests, applies business logic, handles security, and manages data persistence.
  * **MySQL Database:** Stores all application data in a structured manner.

## 🚀 Future Roadmap

This project is designed for scalability and has a clear roadmap for future enhancements to enrich the platform and user experience.

  * **Payment Gateway Integration:** To enable secure online payments for booking deposits.
  * **AI Chatbot:** An intelligent assistant to provide immediate help to users with common questions and site navigation.
  * **Dynamic Pricing:** Suggests optimal rental prices for sellers based on market data and demand analysis.
  * **User Wallet System:** A secure, in-app wallet for managing booking payments and refunds.
  * **Enhanced Security:** Implementation of features like Two-Factor Authentication (2FA) and biometric login for all user roles.
