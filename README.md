# Melu Lingerie E-Commerce Platform

## 🎯 Project Overview

**Domain**: Direct-to-Consumer (DTC) Melu Lingerie e-commerce platform  
**Target Audience**: Women 20-35, fashion-conscious, mobile-first users (80% mobile traffic)  
**Business Model**: E-commerce with subscription boxes, personalization, and community features

## 🏗️ Architecture & Technology Stack

### Backend Architecture
- **Framework**: Spring Boot (Java 21)
- **Architecture Pattern**: Multi-module monolith following MVC and Domain-Driven Design (DDD) principles with hexagonal architecture concepts
- **Database**: PostgreSQL
- **Caching**: Redis (extensive usage planned)
- **File Storage**: AWS S3 Like (Yandex Object Storage)
- **API Versioning**: `/api/v1/` format

### Module Structure
```
├── api/           # REST Controllers layer
├── facade/        # Orchestration layer (only inter-module communication point)
├── users/         # User management domain  
├── media/         # File/image handling domain
├── bootstrap/     # Main application entry point
└── shared/        # Common utilities and interfaces
```

## 🔐 Authentication & Authorization

### Security Model
- **Framework**: Spring Security with JWT tokens
- **Session Management**: Database-stored sessions (`user_sessions` table)
- **Session Expiry**: 24 hours default

### Access Control Levels
```
OPEN ENDPOINTS (Guest + Registered):
- Product catalog viewing
- Product details
- Cart operations (add/remove/modify)
- Cart viewing
- Wishlist management

PROTECTED ENDPOINTS (Registered only):
- Order creation and tracking
- User profile management
- Subscription services
```

## 👤 Guest User Management System

### Core Innovation
**Automatic User Creation**: Every visitor automatically becomes a user with `status=UNREGISTERED` and `role=GUEST`

### Guest-to-Registered Flow
1. **Guest Creation**: Frontend calls backend to create guest user with default params
2. **Associated Data**: Cart, wishlist, user_session, user_device automatically created
3. **Registration**: Update user fields (`status=PENDING_VERIFICATION`, `role=CUSTOMER`)
4. **Verification**: Status changes to verified after device/email verification
5. **No Data Merging**: All data already associated with user_id - just update user fields

### Session Tracking Strategy
- **Storage**: Database (`user_sessions` table)
- **Identification**: Combination of browser fingerprinting, localStorage, and cookies
- **User Linking**: Everything linked by `user_id` from first visit
- **Benefits**: Seamless experience, no data loss, easy activity tracking

## 🚀 New Features Implemented

### Guest User Creation API
- **Endpoint**: `POST /api/v1/users/guests`
- **Purpose**: Automatically creates guest users with sessions, devices, carts, and wishlists
- **Architecture**: Follows the established facade pattern for inter-module communication

#### Request Format
```json
{
  "sessionId": "uuid",
  "deviceInfo": {
    "deviceType": "MOBILE|DESKTOP|TABLET",
    "deviceUuid": "uuid",
    "deviceName": "string",
    "osVersion": "string",
    "browserName": "string",
    "browserVersion": "string",
    "screenWidth": 1920,
    "screenHeight": 1080,
    "screenDensity": 2.0
  }
}
```

#### Response Format
```json
{
  "userId": 123,
  "userSessionId": 456,
  "userDeviceId": 789,
  "cartId": 111,
  "wishlistId": 222,
  "createdAt": "2024-01-01T12:00:00",
  "sessionStatus": "ACTIVE"
}
```

#### Business Logic
1. **Session Check**: If sessionId exists, creates new session for existing user
2. **Device Check**: If deviceUuid exists, creates new session for device owner
3. **New User Creation**: If neither exists, creates completely new user with session and device
4. **Associated Data**: Automatically creates cart and wishlist for new users
5. **Transaction Safety**: All operations performed in single transaction

## 🔧 Development Standards

### Code Quality
- **Testing**: Extensive unit tests, future integration API tests with mocks
- **Validation**: Bean Validation annotations + custom validators
- **Error Handling**: Standardized format with proper HTTP status codes
- **Single Responsibility**: Each class focuses on one specific task

### Data Management
- **Caching Strategy**: Redis for session data, product catalogs, user preferences
- **File Handling**: S3 for product images, user uploads
- **Database**: PostgreSQL with proper indexing for user session queries

## 📊 Monitoring & Analytics

### Observability
- **Metrics**: Prometheus for system metrics
- **Dashboards**: Grafana Cloud for visualization
- **Health Checks**: Actuator endpoints for service monitoring

### Key Metrics to Track
- Guest-to-registered conversion rates
- Session duration and user engagement
- Cart abandonment rates
- Product recommendation effectiveness
- Mobile vs desktop behavior patterns

## 🚀 Deployment & Environment

### Infrastructure
- **Containerization**: Docker images for consistent deployment
- **Database**: PostgreSQL with connection pooling
- **Caching**: Redis cluster for high availability
- **Storage**: S3 for static assets and user uploads

### Environment Configuration
- **Development**: Local or cloud development (with Docker Compose is planned)
- **Production**: Cloud deployment with monitoring stack

## 📋 Development Guidelines

### When generating code, always consider:
1. **Module Boundaries**: Respect facade-only inter-module communication
2. **Guest User Flow**: Account for automatic user creation and session management
3. **Single Responsibility**: Create focused service classes with clear naming
4. **Security Context**: Distinguish between guest and registered user access
5. **Mobile-First**: Ensure responsive design and mobile optimization
6. **DDD Principles**: Maintain domain separation and business logic encapsulation

### Common Patterns to Follow
- Repository pattern for data access
- Service layer for business logic
- DTO pattern for API communication
- Builder pattern for complex object creation
- Strategy pattern for payment/notification services

---

**Last Updated**: 07 August 2025  
**Project Status**: Active Development  
**Team Focus**: Implement MVP with catalog and items cards representation, guest user experience, customer registration and cart management.