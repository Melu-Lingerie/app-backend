```markdown
# DEV_GUIDELINES.md
# Development Guidelines - Lingerie E-Commerce Platform

## 📋 Table of Contents
- [Code Style & Naming Conventions](#code-style--naming-conventions)
- [Architecture & Module Organization](#architecture--module-organization)
- [Database & JPA Patterns](#database--jpa-patterns)
- [API Design Standards](#api-design-standards)
- [Testing Standards](#testing-standards)
- [Security & Authentication](#security--authentication)
- [Caching Strategy](#caching-strategy)
- [Error Handling & Logging](#error-handling--logging)
- [Performance Guidelines](#performance-guidelines)
- [Git & Version Control](#git--version-control)

---

## 🎨 Code Style & Naming Conventions

### Java Naming Standards
```java
// Classes: PascalCase
public class UserCreateService { }
public class ProductResponseDto { }

// Methods & Variables: camelCase
public User createUser(CreateUserRequest request) { }
private String userName;

// Constants: UPPER_SNAKE_CASE
public static final String DEFAULT_USER_STATUS = "UNREGISTERED";
```

### Specialized Naming Patterns
```java
// DTOs: Use suffixes
UserCreateRequestDto;
UserCreateResponseDto;
CartClearFacadeRequestDto;
CartClearFacadeResponseDto;

// Facade Services: Include "Facade" in name
UserCreateFacadeService;
CartManagementFacadeService;

// Repository Methods: Descriptive method names
findByStatusAndCreatedAtBefore(String status, LocalDateTime date);
findByUserIdAndActiveTrue(Long userId);
```

### Package Structure
```
ru.melulingerie.{module-name}
├── controller/          # REST controllers(Placed only in api module)
├── service/            # Business logic services
├── facade/             # Facade services (Separate module with orcestration function)
├── repository/         # Data access layer
├── entity/             # JPA entities
├── dto/               # Data transfer objects
│   ├── request/       # Request DTOs
│   └── response/      # Response DTOs
├── mapper/            # MapStruct mappers
├── config/            # Module-specific configurations
├── exception/         # Custom exceptions
└── enums/            # Enumerations and constants
```

---

##  Architecture & Module Organization

### Module Independence Rules
1. **No Direct Inter-Module Calls**: Modules communicate only through facade-module
2. **Independent DTOs**: Each module has its own DTOs and mappers
3. **Facade Orchestration**: Complex business logic involving multiple modules goes in facade
4. **Identifier-Based Communication**: Modules exchange data using IDs, not entity references

### Service Layer Hierarchy
```java
// Correct flow
@RestController → FacadeService → DomainService(s) → Repository

// Example
UserController → UserCreateFacadeService → UserCreateService + UserSessionCreateService
```

### Configuration Placement
- **Module-specific config**: Place in respective module (e.g., security config in users module)
- **Common configuration**: Place in facade module or bootstrap module
- **Properties**: Centralized in `bootstrap/application.yaml`

### Facade Module Restrictions & Best Practices
```java
@Service
public class UserRegistrationFacadeService {
    // ✅ GOOD: Orchestrate multiple domain services
    public UserRegistrationResponseDto registerUser(UserRegistrationRequestDto request) {
        // Call users module
        UserDto user = userService.updateUserStatus(request.getUserId(), "PENDING_VERIFICATION");
        
        // Call notification module  
        notificationService.sendVerificationEmail(user.getEmail());
        
        // Call session module
        sessionService.updateSessionStatus(request.getSessionId(), "REGISTERED");
        
        return mapToResponse(user);
    }
    
    // ❌ AVOID: Heavy business logic in facade (delegate to domain services)
    // ❌ AVOID: Direct database access from facade
}
```

---

## 🗃️ Database & JPA Patterns

### Naming Conventions
```sql
-- Tables: snake_case
user_sessions, product_categories, order_items

-- Columns: snake_case  
user_id, created_at, is_active

-- Foreign Keys: snake_case
user_id, product_id, session_id

-- Indexes: snake_case
idx_user_sessions_user_id, idx_products_category_status
```

### Entity Design Patterns
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Audit fields - automatic handling
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp  
    private LocalDateTime updatedAt;
    
    // Soft delete pattern
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
    
    @PrePersist
    public void prePersist() {
        if (isDeleted == null) isDeleted = false;
    }
}
```

### Repository Patterns
```java
// Extend JpaRepository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // ✅ Use method naming when possible
    List<User> findByStatusAndCreatedAtBefore(String status, LocalDateTime date);
    Optional<User> findByEmailAndIsDeletedFalse(String email);
    
    // ✅ Use @Query when method naming isn't sufficient
    @Query("SELECT u FROM User u WHERE u.status = :status AND u.lastLoginAt < :date")
    List<User> findInactiveUsers(@Param("status") String status, @Param("date") LocalDateTime date);
}
```

### Entity Relationships
```java
// ✅ GOOD: Cross-module relationships via IDs only
@Entity
public class Cart {
    @Column(name = "user_id")
    private Long userId; // Reference to User from users-module
    
    // ❌ AVOID: Direct entity relationships across modules
    // @ManyToOne User user; 
}

// ✅ GOOD: Within same module relationships
@Entity  
public class CartItem {
    @ManyToOne(fetch = FetchType.LAZY) // Prefer lazy loading
    @JoinColumn(name = "cart_id")
    private Cart cart;
}
```

---

## 🌐 API Design Standards

### Endpoint Conventions
```java
// ✅ REST endpoints: kebab-case
@GetMapping("/api/v1/user-profiles/{id}")
@PostMapping("/api/v1/cart-items")
@PutMapping("/api/v1/wish-list/items/{itemId}")

// ✅ HTTP status codes following standards
@ResponseStatus(HttpStatus.CREATED) // 201 for POST
@ResponseStatus(HttpStatus.NO_CONTENT) // 204 for DELETE
@ResponseStatus(HttpStatus.OK) // 200 for GET/PUT
```

### Request/Response Patterns
```java
// ✅ Standard request handling
@PostMapping("/api/v1/users")
public ResponseEntity<UserResponseDto> createUser(
    @RequestHeader("X-User-Id") Long userId, // Required for most endpoints
    @Valid @RequestBody UserCreateRequestDto request,
    @RequestParam(required = false) String referralCode // Optional params via @RequestParam
) {
    // Implementation
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}

// ✅ Pagination patterns
@GetMapping("/api/v1/products")
public ResponseEntity<List<ProductResponseDto>> getProducts(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "20") int size
    // OR cursor-based pagination
    @RequestParam(required = false) String cursor
) { }
```

### Guest User API Rules
- **X-User-Id Header**: Required for most endpoints (even guest users have user_id)
- **Guest Context**: Pass guest user context through headers, not request body
- **Session Tracking**: Include session information in headers when available

---

## 🧪 Testing Standards

### Test Naming Convention
```java
// Pattern: should_{ExpectedBehavior}_When_{Condition}
@Test
public void should_CreateUser_When_ValidDataProvided() { }

@Test  
public void should_ThrowValidationException_When_EmailIsInvalid() { }

@Test
public void should_ReturnEmptyList_When_NoProductsFound() { }
```

### Test Categories
```java
// Unit Test: Single class with mocked dependencies
@ExtendWith(MockitoExtension.class)
class UserCreateServiceTest {
    @Mock private UserRepository userRepository;
    @InjectMocks private UserCreateService userCreateService;
    
    @Test
    public void should_CreateUser_When_ValidDataProvided() {
        // Test implementation with mocks
    }
}

// Integration Test: Multiple components, real interactions
@SpringBootTest
@Transactional
class UserRegistrationIntegrationTest {
    @Test
    public void should_RegisterUserAndCreateSession_When_ValidRequest() {
        // Test with real database/components
    }
}
```

### Test Package Structure
```
src/test/java/ru/melulingerie/users/
├── service/           # Unit tests for services
├── repository/        # Repository tests  
├── controller/        # Controller tests
└── integration/       # Integration tests
```

---

## 🔐 Security & Authentication

### Method-Level Security
```java
@RestController
public class UserController {
    
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    @GetMapping("/api/v1/users/{id}/orders")
    public ResponseEntity<List<OrderResponseDto>> getUserOrders(@PathVariable Long id) { }
    
    // Open for guests and registered users
    @GetMapping("/api/v1/products")  
    public ResponseEntity<List<ProductResponseDto>> getProducts() { }
}
```

### JWT Token Handling
```java
// Custom JWT service pattern (planned implementation)
@Service
public class JwtTokenService {
    
    public String generateToken(User user) { }
    
    public boolean validateToken(String token) { }
    
    public Long extractUserId(String token) { }
}
```

### Guest-to-Registered User Flow
```java
// Registration endpoint pattern
@PostMapping("/api/v1/auth/register")
public ResponseEntity<AuthResponseDto> register(
    @RequestHeader("X-User-Id") Long guestUserId, // Guest user to convert
    @Valid @RequestBody RegisterRequestDto request
) {
    // Update existing guest user to registered status
    User registeredUser = authFacadeService.convertGuestToRegistered(guestUserId, request);
    return ResponseEntity.ok(mapToResponse(registeredUser));
}
```

---

## 💾 Caching Strategy

### Cache Key Conventions
```java
// Pattern: kebab-case with descriptive prefixes
@Cacheable("user-profiles")
public UserDto getUserProfile(Long userId) { }

@Cacheable(value = "product-catalog", key = "#category + '-' + #page")  
public List<ProductDto> getProductsByCategory(String category, int page) { }

// Redis manual operations
String cacheKey = "cart-items-" + userId;
redisTemplate.opsForValue().set(cacheKey, cartItems, Duration.ofHours(24));
```

### When to Cache
- **Heavy Database Operations**: Complex queries, large result sets
- **Frequently Accessed Data**: Product catalogs, user sessions, popular searches
- **Cross-Module Data**: Data shared between modules through facade

### Cache TTL Guidelines
```java
// Session data: 24 hours (matches session expiry)
@Cacheable(value = "user-sessions", expire = "24h")

// Product data: 1 hour (inventory changes)
@Cacheable(value = "products", expire = "1h")  

// User profiles: 30 minutes
@Cacheable(value = "user-profiles", expire = "30m")
```

---

## 🚨 Error Handling & Logging

### Exception Handling Patterns
```java
// Use specific exceptions when available
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long userId) {
        super("User not found with id: " + userId);
    }

// Custom exceptions for business logic
public class InvalidUserStatusTransitionException extends RuntimeException {
    public InvalidUserStatusTransitionException(String from, String to) {
        super(String.format("Cannot transition user status from %s to %s", from, to));
    }
}

// Global exception handler
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("USER_NOT_FOUND", ex.getMessage()));
    }
}
```

### Logging Standards
```java
@Service
public class UserCreateService {
    private static final Logger logger = LoggerFactory.getLogger(UserCreateService.class);
    
    public User createUser(CreateUserRequest request) {
        logger.debug("Creating user with email: {}", request.getEmail()); // Development debugging
        
        try {
            User user = buildUser(request);
            User savedUser = userRepository.save(user);
            
            logger.info("Successfully created user with id: {}", savedUser.getId()); // Important events
            return savedUser;
            
        } catch (Exception e) {
            logger.error("Failed to create user with email: {}", request.getEmail(), e); // Errors
            throw e;
        }
    }
}
```

### Sensitive Data Handling
```java
// ❌ AVOID: Logging sensitive information
logger.info("User login attempt: {}", loginRequest); // May contain password

// ✅ GOOD: Log only safe information  
logger.info("User login attempt for email: {}", loginRequest.getEmail());
logger.debug("Login request received from IP: {}", request.getRemoteAddr());
```

---

## ⚡ Performance Guidelines

### Database Query Optimization
```java
// ✅ Use native queries for complex operations
@Query(value = "SELECT * FROM users u WHERE u.last_login_at < ?1 AND u.status = ?2", 
       nativeQuery = true)
List<User> findInactiveUsersSince(LocalDateTime date, String status);

// ✅ Bulk operations for large datasets
@Modifying
@Query("UPDATE User u SET u.lastLoginAt = :now WHERE u.id IN :userIds")
void updateLastLoginBulk(@Param("userIds") List<Long> userIds, @Param("now") LocalDateTime now);

// ✅ Use appropriate fetch strategies
@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
private List<CartItem> items;
```

### Redis Usage Patterns
```java
// Session data - prefer Redis for fast access
@Service
public class SessionService {
    
    public void storeSession(String sessionId, SessionData data) {
        String key = "session-" + sessionId;
        redisTemplate.opsForValue().set(key, data, Duration.ofHours(24));
    }
}

// Database vs Redis decision matrix:
// - Transactional data: Database
// - Session/cache data: Redis  
// - Temporary data: Redis
// - Audit data: Database
```

---

## 🔄 Transaction Management

### Transaction Boundaries
```java
// ✅ Facade-level transactions for multi-module operations
@Service
@Transactional
public class UserRegistrationFacadeService {
    
    public UserRegistrationResponseDto registerUser(UserRegistrationRequestDto request) {
        // This method coordinates multiple services in a single transaction
        User user = userService.updateUserStatus(request.getUserId());
        Session session = sessionService.updateSessionForRegistration(request.getSessionId());
        // etc.
    }
}

// ✅ Service-level transactions for single-module operations
@Service
public class UserCreateService {
    
    @Transactional
    public User createUser(CreateUserRequest request) {
        // Single module operation
    }
}
```

### Alternative Transaction Management
```java
// Use TransactionTemplate for programmatic control
@Service
public class ComplexBusinessService {
    
    private final TransactionTemplate transactionTemplate;
    
    public void performComplexOperation() {
        transactionTemplate.execute(status -> {
            // Complex business logic with fine-grained transaction control
            return null;
        });
    }
}
```

---

## 🔧 Configuration Management

### Application Properties
```yaml
# bootstrap/application.yaml - camelCase properties
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/lingerie_db
    username: ${DB_USERNAME:dev_user}
    password: ${DB_PASSWORD:dev_password}
    
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    timeout: 2000ms
    
app:
  jwt:
    secretKey: ${JWT_SECRET:dev-secret-key}
    expirationTime: ${JWT_EXPIRATION:86400} # 24 hours
    
  session:
    defaultTimeout: ${SESSION_TIMEOUT:86400} # 24 hours
    
  guest:
    autoCreateEnabled: true
    defaultRole: GUEST
    defaultStatus: UNREGISTERED
```

### Environment-Specific Configurations
```yaml
# application-dev.yaml
logging:
  level:
    ru.melulingerie: DEBUG
    org.springframework.security: DEBUG

# application-prod.yaml  
logging:
  level:
    ru.melulingerie: INFO
    org.springframework.security: WARN
```

---

## 📝 Documentation Standards

### Code Documentation
```java
// ✅ Document complex business logic
/**
 * Converts guest user to registered user while preserving all associated data.
 * This is our core business logic that differentiates us from other e-commerce platforms.
 * 
 * @param guestUserId ID of the existing guest user
 * @param registrationData registration information
 * @return updated user with registered status
 */
public User convertGuestToRegistered(Long guestUserId, RegistrationData registrationData) {
    // Complex implementation with detailed comments where needed
}

// ✅ Comment ambiguous or complex sections
public void processPayment(PaymentRequest request) {
    // We need to handle this edge case because payment gateway 
    // returns success even when card is declined in some regions
    if (isSpecialRegion(request.getCountry()) && response.getStatus().equals("SUCCESS")) {
        validatePaymentWithSecondaryCheck(request);
    }
}
```

### When NOT to Document
```java
// ❌ Don't document obvious functionality
/**
 * Gets user by ID
 */
public User getUserById(Long id) {
    return userRepository.findById(id);
}

// ✅ Simple, clear method names don't need JavaDoc
public boolean isValidEmail(String email) {
    return emailPattern.matcher(email).matches();
}
```

---

## 🔀 Git & Version Control

### Branching Strategy
```bash
# Feature branch workflow
main
├── develop
├── feature/user-registration-flow
├── feature/cart-management  
├── feature/guest-user-sessions
└── hotfix/security-patch-v1.2.1
```

### Commit Message Standards
```bash
# ✅ Good: Small, descriptive, meaningful commits
git commit -m "Add guest user automatic creation in UserController"
git commit -m "Implement MapStruct mapper for UserCreateRequestDto"  
git commit -m "Fix session expiration validation in SessionService"

# ❌ Avoid: Large, vague commits
git commit -m "Fix stuff"
git commit -m "Update user module with everything"
git commit -m "WIP"
```

### Commit Guidelines
- **Small commits**: Each commit should represent one logical change
- **Descriptive messages**: Explain what was changed and why
- **Present tense**: "Add feature" not "Added feature"
- **Reference issues**: Include ticket numbers when applicable

---

## 🚀 Development Best Practices

### Module Development Checklist
- [ ] Service follows single responsibility principle
- [ ] DTOs are module-specific with proper mappers
- [ ] Inter-module communication goes through facade only
- [ ] Database entities use proper audit fields and soft delete
- [ ] Unit tests cover business logic with appropriate mocking
- [ ] Security annotations applied where needed
- [ ] Caching implemented for heavy operations
- [ ] Error handling with specific exceptions
- [ ] Logging at appropriate levels

### Code Review Checklist
- [ ] Code follows naming conventions
- [ ] No direct inter-module dependencies
- [ ] Proper transaction boundaries
- [ ] Security considerations addressed
- [ ] Performance implications considered
- [ ] Tests provide adequate coverage
- [ ] Error handling is comprehensive
- [ ] Configuration externalized appropriately

---

**Last Updated**: 08 August 2025  
**Version**: 1.0  
**Next Review**: When architectural decisions change or new patterns emerge
```