# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Comprehensive PRODUCT.md documentation with architecture, roadmap, and technical details
- OAuth provider-specific caveats and best practices documentation
- Detailed OAuth connector roadmap (Apple, GitHub, Microsoft, LINE, PAYCO, Toss, etc.)
- Provider usage recommendations by use case (B2C, B2B, Developer tools)

### Changed
- Project modernized to Java 21 (minimum Java 17)
- Updated to HttpClient 5.4.1 with HTTP/2 support
- Migrated to Spring Boot 3.4.1
- Updated all dependencies to latest stable versions
- Improved code quality and consistency

### Fixed
- Security vulnerabilities in dependencies (OWASP Dependency Check)
- Code formatting and naming conventions

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
