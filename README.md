# Insurance Quote Aggregation Service

A Spring Boot application that provides insurance quote aggregation and management capabilities. The service aggregates quotes from multiple insurance providers and offers caching strategies for optimal performance.

## Project Overview

This is a RESTful API-based insurance service built with Spring Boot 3.5.7, featuring:

- **Quote Management**: Create, retrieve, and manage insurance quotes
- **Provider Management**: Manage insurance providers
- **Quote Aggregation**: Aggregate quotes by coverage type with multiple aggregation strategies
- **Advanced Caching**: Redis-based caching using Redisson for improved performance
- **Database Migration**: Liquibase for schema management
- **API Documentation**: Swagger/OpenAPI integration with SpringDoc
- **Logging**: Structured logging with SLF4J and Logback

## Technology Stack

- **Framework**: Spring Boot 3.5.7
- **Java Version**: Java 25
- **Build Tool**: Maven
- **Database**: PostgreSQL
- **Caching**: Redis with Redisson
- **ORM**: Hibernate JPA with Hibernate JCache integration
- **API Documentation**: SpringDoc OpenAPI 2.8.14
- **Code Generation**: MapStruct 1.6.3, Lombok
- **Testing**: JUnit 5, WireMock

## Project Structure

```
src/
├── main/
│   ├── java/insurance/
│   │   ├── InsuranceApplication.java          # Main application entry point
│   │   ├── annotations/
│   │   │   └── CleanAllCaches.java           # Custom annotation for cache invalidation
│   │   ├── aop/
│   │   │   └── cache/
│   │   │       └── CacheAspect.java          # AOP aspect for cache management
│   │   ├── config/
│   │   │   ├── ApplicationProperties.java    # Application configuration properties
│   │   │   ├── CacheConfig.java              # Redis cache configuration
│   │   │   └── CacheNames.java               # Cache name constants
│   │   ├── domain/                           # JPA entities
│   │   │   ├── BaseEntity.java
│   │   │   ├── Provider.java
│   │   │   └── Quote.java
│   │   ├── repository/                       # Data access layer
│   │   │   ├── ProviderRepository.java
│   │   │   └── QuoteRepository.java
│   │   ├── service/                          # Business logic layer
│   │   │   ├── AggregationService.java       # Quote aggregation service
│   │   │   ├── QuoteService.java             # Quote management service
│   │   │   ├── dto/                          # Data transfer objects
│   │   │   ├── strategy/                     # Aggregation strategies
│   │   │   └── mapper/                       # DTO mappers
│   │   └── web/
│   │       └── rest/
│   │           ├── AggregationController.java # Aggregation API endpoints
│   │           ├── QuoteController.java       # Quote API endpoints
│   │           └── errors/                    # Error handling
│   └── resources/
│       ├── application.yml                   # Main configuration
│       ├── application-dev.yml               # Development profile
│       ├── liquibase/                        # Database migrations
│       ├── messages.properties               # Internationalization
│       └── logback.xml                       # Logging configuration
└── test/                                     # Test suite
```

## Getting Started

### Prerequisites

- **Java 25** or higher
- **Maven 3.6+**
- **PostgreSQL 12+**
- **Redis 6.0+** (for caching)

### Setup Instructions

#### 1. Database Setup

PostgreSQL must be running before starting the application.

**Default Configuration:**
- Host: `127.0.0.1`
- Port: `5432`
- Database: `insurance`
- Username: `admin`
- Password: `password`

These can be overridden via environment variables:
```bash
DB_HOST=your-host
DB_PORT=5432
DB_DATABASE=your-database
DB_USER=your-username
DB_PASS=your-password
```

#### 2. Redis Setup

Redis must be running for caching functionality.

**Default Configuration:**
- Host: `127.0.0.1`
- Port: `6379`

#### 3. Build the Application

```bash
# Using Maven wrapper (Windows)
mvnw.cmd clean package

# Using Maven wrapper (Linux/Mac)
./mvnw clean package

# Using system Maven (if installed)
mvn clean package
```

#### 4. Run the Application

```bash
# Run directly from JAR
java -jar target/insurance-0.0.1-SNAPSHOT.jar

# Run with Maven
mvnw.cmd spring-boot:run

# Run with specific profile (dev, prod, etc.)
java -jar target/insurance-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

The application will start on **port 8383** by default.

### Running with Docker Compose

A `docker-compose.yml` file is provided to easily set up PostgreSQL and Redis:

```bash
# Start all services
docker-compose up -d

# Stop all services
docker-compose down
```

## Configuration

### Environment Variables

Key environment variables for configuration:

| Variable | Default | Description |
|----------|---------|-------------|
| `SERVER_PORT` | 8383 | Server port |
| `SERVER_UNDERTOW_THREADS_IO` | 400 | Undertow IO threads |
| `SERVER_UNDERTOW_THREADS_WORKER` | 4000 | Undertow worker threads |
| `LOG_LEVEL` | info | Root logging level |
| `SPRING_ACTIVE_PROFILE` | dev | Active Spring profile |
| `DB_HOST` | 127.0.0.1 | PostgreSQL host |
| `DB_PORT` | 5432 | PostgreSQL port |
| `DB_DATABASE` | insurance | Database name |
| `DB_USER` | admin | Database username |
| `DB_PASS` | password | Database password |
| `LIQUIBASE_ENABLED` | true | Enable/disable Liquibase migrations |
| `DEBUG_SQL` | false | Enable SQL query logging |

### Application Configuration

Main configuration is in `application.yml`:
- Server port and thread configuration
- Spring profiles and active profile
- Liquibase database migration settings
- Jackson serialization settings
- JPA and database configuration

Development-specific settings are in `application-dev.yml`.

## Caching Strategy

### Overview

The application implements a **Redis-based distributed caching strategy** using **Redisson**, providing high-performance data retrieval and reduced database load.

### Cache Configuration

**Cache Provider**: Redisson Spring Cache Manager
- Redis backend for distributed caching
- Automatic serialization of cached objects
- Graceful shutdown on application termination

**Configured Caches**:
1. **AGGREGATED_DATA** (`AggregatedData`)
   - Caches aggregated quote results
   - Key structure: `{aggregationType, coverageType}`
   - Used by: `AggregationService.getAggregatedData()`

2. **QUOTE_CACHE** (`QuoteCache`)
   - Caches individual quote data
   - Reduces database queries for frequently accessed quotes

### Implementation Details

#### Cache Annotations

- **`@Cacheable`**: Retrieves from cache or executes method and caches result
  ```java
  @Cacheable(cacheNames = CacheNames.AGGREGATED_DATA, 
             key = "{#requestDto.aggregationType, #requestDto.coverageTypeDto}")
  public AggregationResultDto getAggregatedData(AggregationRequestDto requestDto)
  ```

- **`@CleanAllCaches`**: Custom annotation to invalidate all caches
  - Applied to methods that modify data (create, update, delete)
  - Ensures cache consistency after data changes

#### Cache Invalidation

The `@CleanAllCaches` annotation is used on write operations:
```java
@Transactional
@CleanAllCaches
public void createQuote(CreateQuoteRequestDto requestDto)
```

This triggers the `CacheAspect` which:
1. Clears all registered caches before method execution
2. Executes the method
3. Returns the result with cache refreshed on next read

#### AOP Implementation

**CacheAspect.java** (`insurance.aop.cache.CacheAspect`):
- Uses Spring AOP to intercept `@CleanAllCaches` annotated methods
- Implements `@Around` advice for pre-execution cache clearing
- Provides logging and error handling

**Key Benefits**:
- Automatic cache invalidation without manual management
- Transactional consistency with database changes
- Aspect-oriented approach keeps cache logic separate from business logic

### Cache Performance Characteristics

| Metric | Value |
|--------|-------|
| **Backend** | Redis (in-memory) |
| **Distribution** | Distributed across instances |
| **Serialization** | Automatic (Redisson) |
| **TTL** | Configurable per cache |
| **Eviction** | LRU (configurable) |

### Best Practices Applied

1. **Cache Naming**: Centralized in `CacheNames.java` for easy maintenance
2. **Key Generation**: Explicit key specifications using SpEL
3. **Invalidation Strategy**: Time-based and event-based (annotation-driven)
4. **Monitoring**: Cache metrics available via Spring Actuator
5. **Graceful Degradation**: Fallback to `NoOpCacheManager` if Redis unavailable

## API Endpoints

### Quote Management

- **GET** `/api/quotes` - Get list of quotes
- **POST** `/api/quotes` - Create new quote
- **GET** `/api/quotes/{id}` - Get quote by ID
- **PUT** `/api/quotes/{id}` - Update quote
- **DELETE** `/api/quotes/{id}` - Delete quote

### Quote Aggregation

- **POST** `/api/aggregation` - Get aggregated quotes
  - Request: `AggregationRequestDto` (aggregationType, coverageType)
  - Response: `AggregationResultDto` (sortedQuotes, best quote)

### Provider Management

- **GET** `/api/providers` - List all providers
- **POST** `/api/providers` - Create new provider
- **GET** `/api/providers/{id}` - Get provider by ID
- **PUT** `/api/providers/{id}` - Update provider
- **DELETE** `/api/providers/{id}` - Delete provider

## API Documentation

Swagger/OpenAPI documentation is available at:

```
http://localhost:8383/swagger-ui.html
http://localhost:8383/v3/api-docs
```

Generated Swagger YAML is available in the project root as `swagger.yaml`.

## Testing

### Running Tests

```bash
# Run all tests
mvnw.cmd test

# Run specific test class
mvnw.cmd test -Dtest=QuoteServiceTest

# Run with coverage
mvnw.cmd clean test jacoco:report
```

### Test Structure

- **Unit Tests**: Service and component logic
- **Integration Tests**: API endpoints with `AbstractBaseIT`
- **Test Utilities**:
  - `AbstractBaseIT`: Base class for integration tests
  - `DataCleaner`: Database cleanup utilities
  - `HttpTestUtil`: HTTP client utilities for testing

### Coverage Reports

JaCoCo coverage reports are generated at:
```
target/site/jacoco/index.html
```

## Logging

### Configuration

Logging is configured via `logback.xml` with:

- **Root Level**: Configurable via `LOG_LEVEL` environment variable (default: `info`)
- **SQL Logging**: Disabled by default, enable with `DEBUG_SQL=true`
- **Output**: Console and file appenders

### Log Levels

```yaml
logging:
  level:
    root: info                    # Default root level
    insurance: debug              # Application package
    org.springframework: warn      # Spring Framework
    org.hibernate: warn           # Hibernate ORM
```

## Database Migrations

Liquibase handles database schema management:

- **Change Log**: `src/main/resources/liquibase/master.xml`
- **Migrations**: `src/main/resources/liquibase/changelog/`
- **Data**: `src/main/resources/liquibase/data/`

Migrations run automatically on application startup.

## Performance Considerations

1. **Caching**: Redis caching significantly reduces database queries
2. **Connection Pooling**: Configured via JPA/Hibernate
3. **Thread Configuration**: 
   - IO Threads: 400 (configurable)
   - Worker Threads: 4000 (configurable)
4. **Database Indexing**: Applied to frequently queried columns
5. **Query Optimization**: Lazy loading and JOIN FETCH strategies

## Common Issues and Solutions

### Issue: "Connection refused" for PostgreSQL
**Solution**: Ensure PostgreSQL is running:
```bash
docker-compose up -d postgresql
```

### Issue: "Connection refused" for Redis
**Solution**: Ensure Redis is running:
```bash
docker-compose up -d redis
```

### Issue: Caches not invalidating
**Solution**: Ensure `@CleanAllCaches` annotation is properly applied to write operations

### Issue: Slow API responses
**Solution**: 
- Check Redis connectivity
- Monitor cache hit rates via metrics endpoint
- Verify database query performance

## Maintenance

### Monitoring Endpoints

Spring Boot Actuator provides monitoring endpoints:

```
http://localhost:8383/actuator
http://localhost:8383/actuator/health
http://localhost:8383/actuator/metrics
http://localhost:8383/actuator/caches
```

### Health Check

```bash
curl http://localhost:8383/actuator/health
```

## Contributing

- Follow Spring Boot best practices
- Use provided mappers for DTO conversion
- Utilize caching annotations for appropriate methods
- Add integration tests for new endpoints

## License

Proprietary - Insurance Service Project

## Support

For issues or questions, contact the development team.

