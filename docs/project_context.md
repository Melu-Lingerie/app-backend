# Melu Lingerie E-Commerce Platform - Technical Specification

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
├── cart/          # Shopping cart domain
├── users/         # User management domain  
├── media/         # File/image handling domain
├── products/      # Product catalog domain
└── shared/        # Common utilities and interfaces
```

### Frontend
- **Framework**: Vue.js SPA
- **Responsive**: Mobile-first design
- **Client Type**: Browser only (mobile-optimized)

### Infrastructure
- **Environments**: Development, Production
- **Deployment**: Docker containers
- **Monitoring**: Prometheus + Grafana Cloud

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

## 👤 Unique Guest User Management System

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

## 🏛️ Domain-Driven Design Implementation

### Bounded Contexts (Gradle Modules)
- **Users**: Authentication, profiles, guest management
- **Products**: Catalog, variants, collections, reviews
- **Cart**: Shopping cart, items, calculations
- **Media**: Image/video storage, processing
- **Orders**: Order lifecycle, payments, fulfillment
- **Subscriptions**: Secret boxes, recurring deliveries

### Communication Rules
- **Inter-module Communication**: Only through facade-module interfaces
- **Single Responsibility**: Each service class has one specific function
- **Naming Convention**: `[Entity][Action]Service` (e.g., `UserCreateService`, `UserSessionCreateService`)

### Service Layer Pattern
```
Controller → FacadeService → DomainService(s) → Repository
Example: UserController → UserCreateFacadeService → UserCreateService + UserSessionCreateService
```

## 🛍️ Business Domain Model

### Core Entities
- **User**: Guest/Registered states, devices, sessions
- **Product**: Bras, panties, sets with variants (size, color, material)
- **Cart**: Guest and registered user carts
- **Order**: Purchase transactions, status workflow
- **Wishlist**: Saved items, sharing capabilities
- **Subscription**: Recurring surprise boxes
- **UserSession**: Activity tracking, device binding

### Key Business Features (Planned)
- **Mix'n'Match Constructor**: Interactive set builder
- **Secret Box**: Personalized surprise subscriptions
- **UGC Gallery**: Customer photo sharing
- **Blogger Collaborations**: Influencer recommendations
- **Gift Cards**: Digital gifting system
- **Online Consultations**: Virtual fitting appointments

## 🔧 Development Standards

### Code Quality
- **Testing**: Extensive unit tests, future integration API tests with mocks
- **Validation**: Bean Validation annotations + custom validators
- **Error Handling**: Standardized format (TBD)
- **Single Responsibility**: Each class focuses on one specific task

### Data Management
- **Caching Strategy**: Redis for session data, product catalogs, user preferences
- **File Handling**: S3 for product images, user uploads
- **Database**: PostgreSQL with proper indexing for user session queries

### Integration Points
- **Email Service**: User verification, order confirmations, marketing
- **SMS Service**: Verification codes, order updates
- **Payment Services**: Multiple payment gateways
- **Social Media APIs**: UGC integration, sharing features

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

## 📋 Development Guidelines for AI Assistance

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
```