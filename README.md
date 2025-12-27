# Insurance Quote Aggregation Service

A Spring Boot–based RESTful service for managing and aggregating insurance quotes from multiple providers.  
The application demonstrates clean architecture, business-driven design, aggregation strategies, and Redis-based caching.

---

## Project Overview

This service manages insurance quotes provided by multiple insurance companies and exposes an aggregation endpoint to determine the “best” quote based on configurable business rules.

### Core Concepts

- **Quote**  
  A price proposal from an insurance provider for a **single coverage type** (e.g., CAR, HEALTH).  
  Only **one active quote per provider and coverage type** is allowed.

- **Provider**  
  An insurance company offering insurance quotes.

- **Aggregation**  
  A read-only operation that compares quotes from multiple providers and determines the “best” quote
  (e.g., lowest price or highest price).

---

## Key Features

- Quote creation, update, retrieval, and soft deletion
- Aggregation of quotes across multiple providers
- Pluggable aggregation logic using the **Strategy Pattern**
- Redis-based caching for aggregation results
- Database schema management with Liquibase
- Clean separation of layers (Controller, Service, Repository, Domain)
- Centralized exception handling and structured logging

---

## Technology Stack

- **Framework**: Spring Boot 3.5.7
- **Java**: Java 25
- **Build Tool**: Maven
- **Database**: PostgreSQL
- **ORM**: Hibernate JPA
- **Caching**: Redis with Redisson
- **Database Migration**: Liquibase
- **Mapping**: MapStruct, Lombok
- **API Documentation**: SpringDoc OpenAPI
- **Testing**: JUnit 5

---

---

## Aggregation Design

### Strategy Pattern

Aggregation behavior is implemented using the **Strategy Pattern**.

Each strategy defines a different way to determine the “best” quote:

- **LowestPriceAggregationStrategy**
- **HighestPriceAggregationStrategy**

This design ensures:
- Compliance with the Open/Closed Principle
- Easy extensibility for new aggregation rules
- No conditional logic inside the aggregation service

### Strategy Selection

The aggregation strategy is selected at runtime based on the request parameter (`aggregationType`).

---

## Caching Strategy

- Aggregation results are cached in **Redis**
- Cache keys include:
  - Aggregation type
  - Coverage type
- Caches are invalidated automatically whenever quotes are:
  - Created
  - Updated
  - Deleted

This ensures **eventual consistency** while significantly improving read performance.

---

## Soft Delete Handling

Quotes are **soft-deleted** using a `deleted_at` timestamp.

### Uniqueness Rule

- Only **active quotes** must be unique per `(provider_id, coverage_type)`
- Soft-deleted quotes do not block the creation of new quotes

This is enforced at the database level by including `deleted_at` in the unique constraint.

---

## API Endpoints

> **Note**  
> The original assignment suggested `GET /quotes` and `GET /quotes/aggregate`.  
> In this implementation, `POST` endpoints are used for aggregation to allow structured request bodies
> and future extensibility.

### Quote Management

- **GET** `/api/quotes`  
  Retrieve all active quotes (optionally filtered by coverage type)

- **POST** `/api/quotes`  
  Create a new quote

- **GET** `/api/quotes/{id}`  
  Retrieve a quote by ID (excluding soft-deleted quotes)

- **PUT** `/api/quotes/{id}`  
  Update an existing quote

- **DELETE** `/api/quotes/{id}`  
  Soft-delete a quote

### Quote Aggregation

- **POST** `/api/aggregation`  
  Aggregate quotes for a given coverage type using a specified aggregation strategy

---

## Getting Started

### Prerequisites

- Java 25+
- Maven 3.6+
- PostgreSQL 12+
- Redis 6+

---

### Running with Docker Compose

A `docker-compose.yml` file is provided to start PostgreSQL and Redis:

```bash
docker-compose up -d
