# sb-oauth-java

[![Release](https://img.shields.io/badge/release-v1.0.0-blue.svg)](https://github.com/ScriptonBasestar-io/sb-oauth-java/releases/tag/v1.0.0)
[![Java CI](https://github.com/ScriptonBasestar-io/sb-oauth-java/actions/workflows/ci.yml/badge.svg)](https://github.com/ScriptonBasestar-io/sb-oauth-java/actions/workflows/ci.yml)
[![CodeQL](https://github.com/ScriptonBasestar-io/sb-oauth-java/actions/workflows/codeql.yml/badge.svg)](https://github.com/ScriptonBasestar-io/sb-oauth-java/actions/workflows/codeql.yml)
[![Coverage](https://img.shields.io/badge/coverage-90%25-brightgreen.svg)](https://github.com/ScriptonBasestar-io/sb-oauth-java/actions/workflows/coverage.yml)
[![Java Version](https://img.shields.io/badge/Java-21-blue)](https://adoptium.net/)
[![Maven Central](https://img.shields.io/badge/Maven%20Central-1.0.0-brightgreen)](https://search.maven.org/)
[![JavaDoc](https://img.shields.io/badge/JavaDoc-Online-green.svg)](https://scriptonbasestar-io.github.io/sb-oauth-java/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

> 🌐 **Languages**: English | [한국어](README.ko.md)

Production-ready OAuth 2.0 Client Library for Java

A comprehensive, secure, and production-ready OAuth 2.0 client library for Java applications with built-in support for Korean OAuth providers (Naver, Kakao) and global providers (Google, Facebook).

## 📋 Table of Contents

- [Features](#-features)
- [Quick Start](#-quick-start)
- [Documentation](#-documentation)
- [Supported Providers](#-supported-oauth-providers)
- [System Requirements](#️-system-requirements)
- [Installation](#-installation)
- [Usage Examples](#-usage-examples)
- [Security](#-security)
- [Contributing](#-contributing)
- [License](#-license)

## ✨ Features

### Core Capabilities
- ☕ **Modern Java 21**: Built on Java 21 with support for virtual threads and modern language features
- 🔒 **Production-Ready Security**: Comprehensive security utilities and OWASP-compliant implementation
- ⚡ **High Performance**: Optimized for throughput (~5,000 state generations/sec)
- 🎯 **Type-Safe API**: Intuitive and type-safe OAuth 2.0 flow implementation
- 🌐 **Multi-Provider Support**: Naver, Kakao, Google, Facebook with extensible architecture
- 🚀 **Spring Boot Auto-Configuration**: Zero-configuration setup with Spring Boot Starter

### Security Features (v1.0.0)
- 🛡️ **SecureStateGenerator**: Cryptographically secure CSRF protection (256-bit entropy)
- 🔐 **RedirectUriValidator**: Open redirect attack prevention with whitelist validation
- 📝 **SensitiveDataMaskingUtil**: Automatic masking of secrets in logs (OWASP-compliant)
- ⚠️ **Rich Exception Hierarchy**: 18 exception classes with detailed error context
- 🔍 **Security Scanning**: Automated CodeQL and OWASP Dependency Check

### Quality & Testing
- ✅ **400+ Unit Tests**: Comprehensive test coverage (90%+)
- 📊 **JaCoCo Coverage**: Continuous coverage tracking with Codecov integration
- 🧪 **Test Framework**: JUnit 5, AssertJ, Mockito for robust testing
- 🔄 **CI/CD Pipeline**: Automated testing, security scanning, and deployment

### Documentation
- 📚 **API Documentation**: Complete JavaDoc with usage examples
- 📖 **Production Guide**: Comprehensive production deployment guide (936 lines)
- 🔒 **Security Policy**: Detailed security configuration and best practices (871 lines)
- 🚢 **Deployment Guide**: Docker, Kubernetes, CI/CD setup (1,277 lines)

> 📝 **Note**: OAuth 1.0a is not supported. Most platforms have migrated to OAuth 2.0.

## 🚀 Quick Start

### Maven Dependency

**Spring Boot Starter** (Recommended):
```xml
<dependency>
    <groupId>org.scriptonbasestar.oauth</groupId>
    <artifactId>integration-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

**Core Library**:
```xml
<dependency>
    <groupId>org.scriptonbasestar.oauth</groupId>
    <artifactId>oauth-client</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle Dependency

```gradle
// Spring Boot Starter
implementation 'org.scriptonbasestar.oauth:integration-spring-boot-starter:1.0.0'

// Core Library
implementation 'org.scriptonbasestar.oauth:oauth-client:1.0.0'
```

### Configuration (application.yml)

```yaml
oauth:
  providers:
    naver:
      client-id: ${NAVER_CLIENT_ID}
      client-secret: ${NAVER_CLIENT_SECRET}
      redirect-uri: http://localhost:8080/oauth/callback/naver
    kakao:
      client-id: ${KAKAO_CLIENT_ID}
      client-secret: ${KAKAO_CLIENT_SECRET}
      redirect-uri: http://localhost:8080/oauth/callback/kakao
```

### Simple Usage (3 steps)

```java
// 1. Inject auto-configured beans
@Autowired
private OAuth2NaverGenerateAuthorizeEndpointFunction authFunction;

@Autowired
private OAuth2NaverAccesstokenFunction tokenFunction;

// 2. Generate authorization URL
State state = stateGenerator.generate();
String authUrl = authFunction.generate(state);
response.sendRedirect(authUrl);

// 3. Exchange code for token
OAuth2NaverTokenRes token = tokenFunction.issue(new Verifier(code), state);
String accessToken = token.getAccessToken();
```

That's it! No manual configuration needed with Spring Boot Starter.

## 📚 Documentation

### Guides

- 📖 **[Production Guide](PRODUCTION_GUIDE.md)** - Production deployment, performance tuning, monitoring
- 🔒 **[Security Policy](SECURITY.md)** - Security configuration, vulnerability prevention, compliance
- 🚢 **[Deployment Guide](DEPLOYMENT.md)** - Docker, Kubernetes, CI/CD pipelines
- 📋 **[Changelog](CHANGELOG.md)** - Release notes and version history

### API Documentation

- 📚 **[JavaDoc (Online)](https://scriptonbasestar-io.github.io/sb-oauth-java/)** - Complete API documentation
- 📝 **[User Guide](docs/USER_GUIDE.md)** - Step-by-step tutorials for beginners
- 🏗️ **[Architecture](docs/ARCHITECTURE.md)** - Internal architecture and design philosophy
- ❓ **[FAQ](docs/FAQ.md)** - Common questions and troubleshooting

### Examples

- 🎯 **[spring-boot-basic](examples/spring-boot-basic/)** - Basic Naver OAuth example
- 🔐 **[spring-boot-security-enhanced](examples/spring-boot-security-enhanced/)** - Production-ready security setup

## 📦 Supported OAuth Providers

| Provider | 문서 | 애플리케이션 등록 |
|----------|------|-------------------|
| **Naver** | [개발 가이드](https://developers.naver.com) | [내 애플리케이션](https://developers.naver.com/apps/#/myapps) |
| **Kakao** | [REST API](https://developers.kakao.com/docs/restapi/user-management) | [내 애플리케이션](https://developers.kakao.com/console/app) |
| **Google** | [OAuth 2.0](https://developers.google.com/identity/protocols/oauth2) | [Cloud Console](https://console.developers.google.com) |
| **Facebook** | [로그인 문서](https://developers.facebook.com/docs/facebook-login) | [앱 대시보드](https://developers.facebook.com/apps) |

## ⚙️ System Requirements

- **Java**: 21 or higher
- **Maven**: 3.9.x or higher
- **Spring Boot**: 3.4.x (for Spring Boot Starter)

## 💻 Installation

### Using Maven

Add to your `pom.xml`:

```xml
<dependencies>
    <!-- Spring Boot Starter (Auto-Configuration) -->
    <dependency>
        <groupId>org.scriptonbasestar.oauth</groupId>
        <artifactId>integration-spring-boot-starter</artifactId>
        <version>1.0.0</version>
    </dependency>

    <!-- Storage Backend (Choose one) -->
    <!-- Redis (Recommended for production) -->
    <dependency>
        <groupId>org.scriptonbasestar.oauth</groupId>
        <artifactId>storage-redis</artifactId>
        <version>1.0.0</version>
    </dependency>

    <!-- OR Ehcache (For single-server deployments) -->
    <!--
    <dependency>
        <groupId>org.scriptonbasestar.oauth</groupId>
        <artifactId>storage-ehcache</artifactId>
        <version>1.0.0</version>
    </dependency>
    -->
</dependencies>
```

### Using Gradle

Add to your `build.gradle`:

```gradle
dependencies {
    // Spring Boot Starter (Auto-Configuration)
    implementation 'org.scriptonbasestar.oauth:integration-spring-boot-starter:1.0.0'

    // Storage Backend (Choose one)
    implementation 'org.scriptonbasestar.oauth:storage-redis:1.0.0'
    // OR
    // implementation 'org.scriptonbasestar.oauth:storage-ehcache:1.0.0'
}
```

## 📖 Usage Examples

### Basic Example

See [examples/spring-boot-basic](examples/spring-boot-basic/) for a complete working example.

```java
@RestController
public class OAuthController {

    @Autowired
    private OAuth2NaverGenerateAuthorizeEndpointFunction naverAuthFunction;

    @Autowired
    private OAuth2NaverAccesstokenFunction naverTokenFunction;

    @Autowired
    private StateGenerator stateGenerator;

    // Step 1: Redirect to OAuth provider
    @GetMapping("/oauth/naver/login")
    public void login(HttpServletResponse response) throws IOException {
        State state = stateGenerator.generate();
        String authUrl = naverAuthFunction.generate(state);
        response.sendRedirect(authUrl);
    }

    // Step 2: Handle callback
    @GetMapping("/oauth/callback/naver")
    public String callback(@RequestParam String code, @RequestParam String state) {
        OAuth2NaverTokenRes token = naverTokenFunction.issue(
            new Verifier(code),
            new State(state)
        );
        return "Access Token: " + token.getAccessToken();
    }
}
```

### Security-Enhanced Example

See [examples/spring-boot-security-enhanced](examples/spring-boot-security-enhanced/) for production-ready security setup.

```java
@Configuration
public class SecurityConfig {

    @Bean
    public StateGenerator stateGenerator() {
        // Production-optimized: 256-bit entropy, timestamp-based expiration
        return SecureStateGenerator.forProduction();
    }

    @Bean
    public RedirectUriValidator redirectUriValidator() {
        // Whitelist validation, HTTPS enforcement
        return new RedirectUriValidator(
            Set.of("https://yourdomain.com/oauth/callback"),
            false, // allowLocalhost
            true   // requireHttps
        );
    }
}
```

## 🔒 Security

### Security Features

- **CSRF Protection**: Cryptographically secure state parameter (256-bit entropy)
- **Open Redirect Prevention**: Whitelist-based redirect URI validation
- **Secure Logging**: Automatic masking of sensitive data in logs
- **HTTPS Enforcement**: Production-ready SSL/TLS configuration
- **OWASP Compliance**: Follows OWASP Top 10 security guidelines
- **OAuth 2.0 Security BCP**: Compliant with [RFC 6749](https://tools.ietf.org/html/rfc6749) and [Security Best Current Practice](https://tools.ietf.org/html/draft-ietf-oauth-security-topics)

### Vulnerability Reporting

See [SECURITY.md](SECURITY.md) for vulnerability reporting process.

**Email**: security@scriptonbasestar.org

## 🏗️ Architecture

### Module Structure

```
sb-oauth-java/
├── oauth-client/              # Core OAuth client
├── oauth-connector/           # Provider implementations
│   ├── connector-naver/       # Naver OAuth connector
│   ├── connector-kakao/       # Kakao OAuth connector
│   ├── connector-google/      # Google OAuth connector
│   └── connector-facebook/    # Facebook OAuth connector
├── oauth-storage/             # Storage backends
│   ├── storage-redis/         # Redis storage
│   └── storage-ehcache/       # Ehcache storage
├── oauth-integration/         # Framework integrations
│   └── integration-spring-boot-starter/  # Spring Boot starter
└── examples/                  # Example applications
```

See [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) for detailed architecture documentation.

## 🤝 Contributing

Contributions are welcome! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

### Development Setup

1. **Clone repository**:
   ```bash
   git clone https://github.com/ScriptonBasestar-io/sb-oauth-java.git
   cd sb-oauth-java
   ```

2. **Build project**:
   ```bash
   mvn clean install
   ```

3. **Run tests**:
   ```bash
   mvn test
   ```

4. **Generate coverage report**:
   ```bash
   mvn jacoco:report
   ```

## 📝 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## 🌟 Star History

If you find this project useful, please consider giving it a star ⭐

[![Star History Chart](https://api.star-history.com/svg?repos=ScriptonBasestar-io/sb-oauth-java&type=Date)](https://star-history.com/#ScriptonBasestar-io/sb-oauth-java&Date)

## 📧 Contact

- **GitHub Issues**: [Issues](https://github.com/ScriptonBasestar-io/sb-oauth-java/issues)
- **Email**: support@scriptonbasestar.org
- **Website**: https://scriptonbasestar.org

---

**Made with ❤️ by [ScriptonBaseStar](https://github.com/ScriptonBasestar-io)**
