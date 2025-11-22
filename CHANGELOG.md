# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Test coverage badge in README.md
- Code quality badge in README.md
- Recent improvements section in README.md with detailed change log
- 51 new unit tests across core OAuth classes
- Comprehensive test suite for utility classes (SBStringDigestUtil, OAuthBaseConfig)
- Test coverage for OAuth 2.0 authorization endpoint generation
- Getter methods for OAuth20AccessTokenConfig, OAuth20AuthorizeTokenConfig, OAuth20ResourceConfig

### Changed
- Updated Java version badge to emphasize Java 21 LTS support
- Improved README.md structure and documentation
- Test coverage increased from 18% to 40% (+22%)
- Total unit tests increased from 68 to 119 (+51 tests, +75%)

### Fixed
- OAuth20Constants.REFRESH_TOKEN typo: `refesh_token` → `refresh_token` (OAuth 2.0 spec compliance)

### Removed
- All deprecated API usage (Apache HttpClient 5.x, Jackson)
- All unchecked operation warnings
- All SpotBugs UUF_UNUSED_FIELD warnings (15+ warnings resolved)
- All Checkstyle violations (1,809 → 0)

## [sb-oauth-20251117-1-DEV] - 2025-01-22

### Added
- Java 21 LTS support
- Unit tests with Jacoco coverage reporting
- Checkstyle code style validation

### Changed
- Upgraded SpotBugs to 4.9.8.1 for Java 21 compatibility
- Migrated from Lombok to manual implementations
- Refactored HttpClient usage to non-deprecated APIs
- Improved Jackson API usage with PropertyNamingStrategies

### Fixed
- 1,809 Checkstyle violations
- Multiple deprecated API warnings
- Unchecked operation compiler warnings

### Removed
- Lombok dependency (replaced with manual getters/setters/constructors)

## Previous Releases

### [2.0.0] - Earlier
- Initial OAuth 2.0 client library
- Support for Naver, Kakao, Google, Facebook providers
- Java 17 support
- Apache HttpClient 5.x integration
- Basic OAuth 2.0 flow implementation

---

## Migration Guide

### Java 21 Migration
- Ensure JDK 21 is installed
- Update JAVA_HOME environment variable
- No code changes required for existing users

### API Changes
- `OAuth20Constants.REFRESH_TOKEN` now returns `"refresh_token"` (was `"refesh_token"`)
- Config model classes now have public getters for all fields

### Dependency Updates
- Apache HttpClient 5.x: Uses non-deprecated execute() methods
- Jackson: Uses PropertyNamingStrategies instead of PropertyNamingStrategy

---

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for contribution guidelines.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
