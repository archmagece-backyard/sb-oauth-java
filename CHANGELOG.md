# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2024-11-19

### Added

- **Security Utilities** (oauth-client/src/main/java/org/scriptonbasestar/oauth/client/security/)
  - **SecureStateGenerator**: Cryptographically secure state generation
    - 256-bit entropy (32 bytes) using SecureRandom
    - Optional timestamp inclusion for expiration validation
    - Thread-safe implementation with configurable parameters
    - Performance: ~5,000 ops/sec, ~1ms latency
    - Production-optimized factory method
  - **RedirectUriValidator**: Open redirect attack prevention
    - Whitelist-based redirect URI validation
    - HTTPS enforcement (configurable for development)
    - Localhost exemption for local development
    - Comprehensive URI scheme and host validation
    - 32 unit tests with edge cases
  - **SensitiveDataMaskingUtil**: Secure logging utilities
    - Automatic masking for client secrets (ab****89 pattern)
    - Access token masking in logs
    - Refresh token masking
    - Authorization code masking
    - Configurable prefix/suffix length
    - OWASP-compliant logging practices
    - 29 unit tests covering all masking scenarios
  - **Total**: 3 utility classes, 91 unit tests, 1,322 lines of production code

- **Comprehensive Exception Hierarchy** (oauth-client/src/main/java/org/scriptonbasestar/oauth/client/exception/)
  - **OAuth2Exception** base class with rich context
    - Error code support (standardized OAuth 2.0 error codes)
    - Provider information tracking
    - Timestamp tracking for debugging
    - Context map for additional debug information
    - Method chaining for fluent API
  - **OAuth2ConfigurationException** hierarchy
    - InvalidClientException (invalid_client error code)
    - InvalidRedirectUriException (invalid_request error code)
    - MissingConfigurationException (server_error error code)
  - **OAuth2AuthorizationException** hierarchy
    - InvalidGrantException (invalid_grant error code)
    - InvalidStateException (invalid_request error code)
    - AccessDeniedException (access_denied error code)
    - StateExpiredException (invalid_request error code)
  - **OAuth2TokenException** hierarchy
    - TokenExpiredException (invalid_grant error code)
    - TokenRevokedException (invalid_token error code)
    - InvalidTokenException (invalid_token error code)
    - TokenRefreshException (invalid_grant error code)
  - **OAuth2NetworkException** hierarchy
    - ConnectionTimeoutException (server_error error code)
    - NetworkFailureException (server_error error code)
  - **Total**: 18 exception classes with 59 unit tests, 1,452 lines of code

- **API Documentation** (JavaDoc)
  - **security/package-info.java**: Comprehensive security utilities documentation
    - SecureStateGenerator usage examples
    - RedirectUriValidator configuration guide
    - SensitiveDataMaskingUtil best practices
    - Production deployment checklist
    - Security considerations and OWASP compliance
    - 168 lines of detailed documentation
  - **exception/package-info.java**: Complete exception hierarchy documentation
    - ASCII art hierarchy diagram
    - Usage examples for all 18 exception classes
    - Error handling best practices by category
    - OAuth 2.0 error code reference
    - Category-based exception catching patterns
    - 283 lines of comprehensive documentation
  - **Maven JavaDoc Plugin**: Configured for Java 21
    - UTF-8 encoding for all output
    - Links to Java SE 21 and Jackson documentation
    - Automatic JavaDoc JAR generation
    - Public API visibility only

- **Production Documentation** (3,084 lines total)
  - **PRODUCTION_GUIDE.md** (936 lines)
    - Production environment setup (Java 21, JVM tuning, environment variables)
    - Performance tuning (Redis connection pools, HTTP client, caching strategies)
    - Monitoring and logging (Prometheus metrics, Grafana dashboards, structured logging)
    - High availability (NGINX load balancing, Redis Sentinel/Cluster, graceful shutdown)
    - Troubleshooting guide (invalid state errors, connection timeouts, memory issues)
    - Maintenance procedures (backup/recovery, log rotation, dependency updates)
    - Performance benchmarks on AWS EC2 t3.medium
  - **SECURITY.md** (871 lines)
    - Security configuration guide (StateGenerator, RedirectUriValidator, credential management)
    - Vulnerability prevention (CSRF, XSS, Open Redirect, SQL Injection, timing attacks)
    - Sensitive data handling (data classification, logging guidelines, storage security)
    - Security checklist (pre-production, post-deployment, ongoing maintenance)
    - Security reporting (vulnerability disclosure, severity levels, response times)
    - Compliance documentation (OWASP Top 10, OAuth 2.0 Security BCP)
  - **DEPLOYMENT.md** (1,277 lines)
    - Docker deployment (multi-stage Dockerfile, Docker Compose with Redis and NGINX)
    - Kubernetes deployment (Deployment, Service, Ingress, HPA, Redis StatefulSet)
    - CI/CD pipeline (GitHub Actions, GitLab CI with coverage reporting)
    - Zero-downtime deployment (Rolling Update, Blue-Green, Canary strategies)
    - Rollback strategy and deployment checklist

- **Comprehensive Unit Tests** (400+ tests total, 90%+ coverage)
  - **Core Client Tests**: 122 tests for authorization flow, token exchange, state management
  - **Connector Module Tests**: 61 tests
    - Naver connector: 15 tests
    - Kakao connector: 15 tests
    - Google connector: 16 tests
    - Facebook connector: 15 tests
  - **Storage Backend Tests**: 20 tests
    - Redis storage: 10 tests
    - Ehcache storage: 10 tests
  - **Security Utilities Tests**: 91 tests
    - SecureStateGenerator: 30 tests
    - RedirectUriValidator: 32 tests
    - SensitiveDataMaskingUtil: 29 tests
  - **Exception Hierarchy Tests**: 59 tests
  - **Authorization Functions Tests**: 44 tests
  - **Test Framework**: JUnit 5, AssertJ, Mockito

- **Spring Boot Security Enhanced Example** (examples/spring-boot-security-enhanced/)
  - Production-ready security configuration
  - SecureStateGenerator integration with forProduction() factory
  - RedirectUriValidator with HTTPS enforcement
  - SensitiveDataMaskingUtil in logging configuration
  - Complete SecurityConfig.java with all security beans
  - Comprehensive README with security features documentation
  - Environment-based configuration (.env.example)
  - 5 files, 547 lines of example code and documentation

- **Spring Boot Auto-Configuration Starter** (integration-spring-boot-starter)
  - Zero-configuration OAuth integration via application.yml properties
  - Auto-configured beans for all 4 providers (Naver, Kakao, Google, Facebook)
  - Flexible storage backends: Local (dev), Redis (production), Ehcache (single-server)
  - Type-safe properties with @ConfigurationProperties
  - IDE autocomplete support via spring-configuration-metadata.json
  - META-INF auto-configuration discovery for Spring Boot 3.x
  - Comprehensive 900+ line README with usage examples and troubleshooting
- **Comprehensive Integration Tests** (40+ test cases)
  - OAuth2Properties binding tests with nested properties validation
  - Provider auto-configuration tests (Naver, Kakao, Google, Facebook)
  - Storage auto-configuration tests (Local, Redis, Ehcache)
  - Conditional bean creation verification (@ConditionalOnProperty, @ConditionalOnClass)
  - Failure scenario testing (missing required properties)
  - Custom bean override testing
- **Code Quality Tools and Automation**
  - Checkstyle 10.20.2 with Google Java Style Guide (120 char line length)
  - SpotBugs 4.8.6 for static bug detection and security vulnerabilities
  - PMD 7.7.0 for code quality analysis and best practices
  - JaCoCo 0.8.12 for test coverage tracking (50% minimum per package)
  - Pre-commit hooks for local validation (Checkstyle blocking, SpotBugs/PMD warnings)
  - CI/CD integration with dedicated 'code-quality' job in GitHub Actions
  - Codecov integration for coverage reports
  - CODE_QUALITY.md documentation (500+ lines)
- **Example Projects for OAuth Integration**
  - spring-boot-basic: Fully functional Naver OAuth example (15 files, 1000+ lines)
    - Complete Spring Boot + Thymeleaf application
    - Login, callback, profile, and logout flow
    - Session-based token management
    - Updated to use Spring Boot Starter (zero manual configuration)
  - multi-provider: README with multi-provider architecture patterns
  - redis-storage: README with production Redis setup and Docker Compose
- **Comprehensive User Documentation** (5,200+ lines total)
  - USER_GUIDE.md: Step-by-step tutorials for beginners (1,600+ lines)
    - Basic OAuth concepts and terminology
    - Two complete tutorials (basic Java + Spring Boot)
    - Real-world use cases and code samples
  - MIGRATION.md: Migration guide from Spring Security OAuth and ScribeJava (1,100+ lines)
    - Feature comparison tables
    - Before/after code examples
    - Migration checklist and gotchas
  - ARCHITECTURE.md: Internal architecture and design philosophy (1,200+ lines)
    - Design principles and patterns
    - Module structure and dependencies
    - Core interfaces and extension points
  - FAQ.md: Common questions and troubleshooting (1,300+ lines)
    - Setup, OAuth flow, errors, tokens, security, performance
    - Provider-specific FAQ sections
- **Module-Level README Files** (7 comprehensive READMEs)
  - oauth-client: Core client library documentation
  - connector-naver: Naver OAuth 2.0 integration guide with permanent refresh tokens
  - connector-kakao: Kakao OAuth 2.0 integration guide with optional client_secret
  - connector-google: Google OAuth 2.0 integration guide with OIDC support
  - connector-facebook: Facebook OAuth 2.0 integration guide with Graph API
  - storage-redis: Redis token storage implementation with Jedis
  - storage-ehcache: Ehcache token storage implementation with JSR-107
- **Enhanced Development Environment Setup**
  - Comprehensive .editorconfig with Java 21 settings (120 char line length)
  - IDE_SETUP.md with IntelliJ IDEA, Eclipse, and VS Code configuration
  - Maven wrapper (mvnw) for consistent builds across environments
- **Contribution Guidelines and Community**
  - CONTRIBUTING.md with development workflow and coding standards
  - Pull request template with checklist
  - Issue templates: Bug Report, Feature Request, Documentation Improvement
  - CODE_OF_CONDUCT.md based on Contributor Covenant 2.1
- **Release Automation and Maven Central Deployment**
  - GitHub Actions workflow for automated releases (.github/workflows/release.yml)
  - GPG signing configuration for artifacts
  - OSSRH (Sonatype) staging and release automation
  - Automated version bumping and changelog generation
  - Release notes generation from commit history
- **Product Documentation**
  - PRODUCT.md with comprehensive project overview
  - OAuth provider roadmap (Apple, GitHub, Microsoft, LINE, PAYCO, Toss)
  - Feature matrix and comparison tables
  - Architecture diagrams and design decisions
  - Provider-specific caveats and best practices

### Changed
- **Modernized to Java 21** (minimum Java 17)
  - Virtual threads support
  - Pattern matching and records
  - Sealed classes and interfaces
  - Enhanced switch expressions
- **Upgraded to Spring Boot 3.4.1** from 2.x
  - Jakarta EE 9+ namespace (javax → jakarta)
  - Native compilation support with GraalVM
  - Improved observability and metrics
  - Enhanced autoconfiguration
- **Updated all dependencies to latest stable versions**
  - Jackson 2.18.2 (was 2.15.x)
  - Apache HttpClient 5.4.1 with HTTP/2 support (was 4.x)
  - Netty 4.1.116.Final (was 4.1.90)
  - JUnit 5.11.4 (was 5.9.x)
  - SLF4J 2.0.16 (was 1.7.x)
  - Logback 1.5.15 (was 1.4.x)
- **Enhanced example project with Spring Boot Starter**
  - Removed manual OAuthConfig.java (no longer needed)
  - Simplified application.yml configuration (oauth.* → oauth2.providers.*)
  - Updated README to reflect auto-configuration approach
  - Emphasized zero-configuration benefits
- **Improved code organization and consistency**
  - Consistent package structure across all modules
  - Standardized naming conventions for classes and methods
  - Better separation of concerns (config, connector, storage)
  - Unified error handling patterns

### Fixed
- **Added missing dependency versions in parent POM**
  - Spring Boot dependencies (starter, autoconfigure, configuration-processor, test)
  - Resolved all submodule build failures
  - Ensured consistent dependency resolution across modules
  - Fixed Maven Central deployment issues
- **Network resilience in CI/CD pipeline**
  - Retry logic with exponential backoff for git operations (2s, 4s, 8s, 16s)
  - Improved error handling for transient network failures
  - Better timeout configuration for long-running operations

### Security
- GPG signing for all Maven Central releases
- Secure credential handling in CI/CD (GitHub Secrets)
- Pre-commit hooks to prevent committing secrets (.env, credentials.json)
- OAuth state parameter for CSRF protection in all examples
- Token storage best practices documentation
- Security scanning with OWASP Dependency Check

## [sb-oauth-20181219-3-DEV] - 2024

### Added
- Naver OAuth 2.0 connector
- Kakao OAuth 2.0 connector
- Google OAuth 2.0 connector
- Facebook OAuth 2.0 connector
- Redis token storage implementation
- Ehcache token storage implementation
- Local memory token storage
- Spring Boot integration skeleton
- CSRF protection with state parameter
- Refresh token support
- Token revocation support

### Security
- HTTPS enforcement for production
- State parameter validation for CSRF protection
- Secure token storage patterns
- OWASP Dependency Check integration
- Dependabot automated dependency updates

### Infrastructure
- GitHub Actions CI/CD pipeline
- Java 17 & 21 matrix builds
- Maven multi-module structure
- Automated security scanning

## [Previous Versions]

Previous versions were not formally released to Maven Central.
Development history available in git log.

---

## Version Scheme

- **MAJOR**: Breaking changes (e.g., Java version upgrade, API changes)
- **MINOR**: New features, new OAuth providers
- **PATCH**: Bug fixes, security patches, dependency updates

## Upcoming Releases

### v1.1.0 (2025 Q1) - Planned
- Spring Security integration
- Advanced token management (auto-refresh)
- Additional provider support (Apple, GitHub)

### v1.2.0 (2025 Q2) - Planned
- Microsoft / Azure AD OAuth
- LINE Login
- PAYCO
- Enhanced monitoring and metrics

### v2.0.0 (2025 Q3) - Planned
- OpenID Connect (OIDC) full support
- JWT validation and verification
- Breaking API improvements based on community feedback

See [PRODUCT.md](PRODUCT.md) for detailed roadmap.

---

**Full Changelog**: https://github.com/ScriptonBasestar-io/sb-oauth-java/commits/v1.0.0
