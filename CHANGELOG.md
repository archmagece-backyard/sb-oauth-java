# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
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

### v2.0.0 (2025 Q1) - Planned
- Maven Central release
- 80%+ test coverage
- Complete documentation
- Production-ready

### v2.1.0 (2025 Q2) - Planned
- Spring Boot Auto Configuration
- Spring Security integration
- Spring Boot Starter module

### v2.2.0 (2025 Q3) - Planned
- Apple Sign In
- GitHub OAuth
- Microsoft / Azure AD
- LINE Login
- PAYCO
- Toss

See [PRODUCT.md](PRODUCT.md) for detailed roadmap.
