# Restful Booker API Test Automation 🚀

Automated API Testing framework for the [Restful-Booker](https://restful-booker.herokuapp.com/) demo service.  
This repository demonstrates **Fluent API design**, **Allure reporting**, and **schema validation** using Java.

---

## 📖 Table of Contents
- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Features](#features)
- [Maven Properities](#Maven-Propertiese)
- [Project Structure](#project-structure)
- [Setup & Installation](#setup--installation)
- [Execution](#execution)
- [Sample Scenarios](#sample-scenarios)
- [Allure Reporting](#allure-reporting)
- [Schemas](#schemas)
- [Contribution](#contribution)
- [License](#license)

---

## 🔎 Overview

The project automates **end-to-end lifecycle testing** of the Restful Booker APIs:
- Create Booking
- Read Booking
- Update Booking
- Delete Booking
- Authentication

Each API call is chained in a **Fluent Interface style** and logged into **Allure reports** with full request/response details.

---

## 🛠 Tech Stack

- **Language:** Java 21+
- **Build Tool:** Maven
- **Testing Framework:** TestNG
- **HTTP Client:** RestAssured
- **Reporting:** Allure
- **Schema Validation:** JSON Schema
- **Status Email:** jakarta.mail 

---

## ✨ Features

- ✅ **Fluent API Design** – Clean chaining of operations (`create → validate → update → delete`)  
- ✅ **Authentication Support** – Handles token creation & usage seamlessly  
- ✅ **Schema Validation** – Ensures responses match expected contract  
- ✅ **Custom RestAssured Filter** – Attaches every request/response to Allure  
- ✅ **Data Builders (POJOs + Factories)** – Generate reusable and valid test data  
- ✅ **Listeners Integration** – Centralized logging and failure capture in TestNG  

---
## 📦 Maven Properties

This project centralizes dependency versions in `pom.xml` using Maven properties for easier maintenance:

```xml
<properties>
    <!-- Java version -->
    <maven.compiler.source>21</maven.compiler.source>
    <maven.compiler.target>21</maven.compiler.target>

    <!-- Logging -->
    <log4j.version>2.24.3</log4j.version>

    <!-- Reporting -->
    <allure.version>2.29.1</allure.version>

    <!-- Utilities -->
    <commons-io.version>2.18.0</commons-io.version>
    <jakarta.version>2.0.1</jakarta.version>
    <lombok.version>1.18.38</lombok.version>

    <!-- Validation & Serialization -->
    <json-schema-validator.version>5.5.6</json-schema-validator.version>
    <jackson-databind.version>2.20.0</jackson-databind.version>

    <!-- API Testing -->
    <rest-assured.version>5.5.6</rest-assured.version>

    <!-- Testing Framework -->
    <testng.version>7.11.0</testng.version>
</properties>

---

# 📦 Project Structure

RestfulBookerAPIs-TestAutomationRepo/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── clients/
│   │   │   │   └── RestClient.java              # Configures RestAssured + Allure filter
│   │   │   ├── pojos/
│   │   │   │   ├── Booking.java
│   │   │   │   ├── BookingDates.java
│   │   │   │   ├── AuthRequest.java
│   │   │   │   └── AuthResponse.java
│   │   │   ├── factories/
│   │   │   │   └── BookingFactory.java          # Test data builder for Bookings
│   │   │   ├── repositories/
│   │   │   │   ├── AuthApi.java                 # /auth token management
│   │   │   │   └── BookingApi.java              # CRUD + Fluent API operations
│   │   │   └── utils/
│   │   │       └── AllureRestAssuredFilter.java # Logs HTTP cycles to Allure
│   │   └── resources/
│   │       ├── booking-schema.json              # Schema for booking object
│   │       ├── create-booking-schema.json       # Schema for create response
│   │       └── update-booking-schema.json       # Schema for update response
│   └── test/
│       ├── java/
│       │   └── tests/
│       │       └── BookingTest.java             # Example lifecycle test scenario
│       └── resources/                           # (Optional) test resources
├── pom.xml                                      # Maven build file
└── README.md                                    # Project documentation


