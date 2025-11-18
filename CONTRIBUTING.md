# Contributing to sb-oauth-java

Thank you for your interest in contributing to sb-oauth-java! This document provides guidelines and instructions for contributing.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [How Can I Contribute?](#how-can-i-contribute)
- [Development Setup](#development-setup)
- [Coding Standards](#coding-standards)
- [Commit Guidelines](#commit-guidelines)
- [Pull Request Process](#pull-request-process)
- [Adding a New OAuth Provider](#adding-a-new-oauth-provider)
- [Testing Guidelines](#testing-guidelines)
- [Documentation](#documentation)

## Code of Conduct

This project adheres to a Code of Conduct. By participating, you are expected to uphold this code. Please report unacceptable behavior to archmagece@gmail.com.

## How Can I Contribute?

### Reporting Bugs

Before creating bug reports, please check existing issues. When creating a bug report, include:

- **Clear title and description**
- **Steps to reproduce**
- **Expected vs actual behavior**
- **Environment details** (Java version, OS, etc.)
- **Code samples** (if applicable)
- **Error messages and stack traces**

### Suggesting Enhancements

Enhancement suggestions are tracked as GitHub issues. When creating an enhancement suggestion, include:

- **Clear use case**
- **Current behavior vs desired behavior**
- **Why this enhancement would be useful**
- **Possible implementation approach**

### Contributing Code

1. **Fork the repository**
2. **Create a feature branch** (`git checkout -b feature/amazing-feature`)
3. **Make your changes**
4. **Write tests** for your changes
5. **Ensure all tests pass** (`mvn test`)
6. **Commit your changes** (following commit guidelines)
7. **Push to your fork** (`git push origin feature/amazing-feature`)
8. **Open a Pull Request**

## Development Setup

### Prerequisites

- **Java**: 17 or 21 (recommended)
- **Maven**: 3.9.x or higher
- **Git**: Latest version
- **IDE**: IntelliJ IDEA, Eclipse, or VS Code with Java extensions

> 📖 **Detailed IDE setup instructions**: See [docs/IDE_SETUP.md](docs/IDE_SETUP.md) for IDE-specific configuration guides.

### Getting Started

```bash
# Clone the repository
git clone https://github.com/ScriptonBasestar-io/sb-oauth-java.git
cd sb-oauth-java

# Build the project
mvn clean install

# Run tests
mvn test

# Skip tests for faster build
mvn clean install -DskipTests=true
```

### Project Structure

```
sb-oauth-java/
├── oauth-client/           # Core OAuth 2.0 client library
├── oauth-connector/        # Provider-specific implementations
│   ├── connector-naver/
│   ├── connector-kakao/
│   ├── connector-google/
│   └── connector-facebook/
├── oauth-storage/          # Token storage implementations
│   ├── storage-redis/
│   └── storage-ehcache/
├── oauth-integration/      # Framework integrations
│   └── integration-spring/
└── test-helper/            # Testing utilities
```

## Coding Standards

### Java Style Guide

- **Indentation**: Use tabs (as configured in .editorconfig)
- **Line length**: 120 characters maximum
- **Naming conventions**:
  - Classes: `PascalCase`
  - Methods: `camelCase`
  - Constants: `UPPER_SNAKE_CASE`
  - Packages: `lowercase`

### Code Quality

- **Use Lombok** for boilerplate code reduction
- **Avoid null**: Use `Optional` where appropriate
- **Immutability**: Prefer immutable objects
- **Exception handling**: Use custom exceptions from `org.scriptonbasestar.oauth.client.exception`
- **Logging**: Use SLF4J for logging

### Example Code Style

```java
@Getter
@Builder
public class OAuth2ExampleConfig extends OAuthBaseConfig {
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final String scope;

    @Override
    public String getAuthorizeEndpoint() {
        return "https://example.com/oauth/authorize";
    }
}
```

## Commit Guidelines

We follow [Conventional Commits](https://www.conventionalcommits.org/) specification.

### Commit Message Format

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types

- **feat**: New feature
- **fix**: Bug fix
- **docs**: Documentation changes
- **style**: Code style changes (formatting, missing semicolons, etc.)
- **refactor**: Code refactoring
- **perf**: Performance improvements
- **test**: Adding or updating tests
- **build**: Build system or dependency changes
- **ci**: CI configuration changes
- **chore**: Other changes that don't modify src or test files

### Examples

```bash
# Feature
git commit -m "feat(connector): add Apple Sign In support"

# Bug fix
git commit -m "fix(naver): resolve refresh token expiration issue"

# Documentation
git commit -m "docs(readme): update Spring Boot integration guide"

# With body
git commit -m "feat(storage): add PostgreSQL token storage

Implement PostgreSQL-based token storage for distributed systems.
Includes migration scripts and connection pooling support.

Closes #123"
```

### Scope

Use the module name or component:
- `client` - Core client
- `connector` - Connector modules
- `naver`, `kakao`, `google`, `facebook` - Specific providers
- `storage` - Storage modules
- `integration` - Integration modules
- `build` - Build configuration
- `ci` - CI/CD

## Pull Request Process

### Before Submitting

1. **Update documentation** if you've changed APIs
2. **Add tests** for new features
3. **Ensure all tests pass** (`mvn test`)
4. **Update CHANGELOG.md** if applicable
5. **Run code formatting** (if configured)

### PR Title

Follow the same format as commit messages:

```
feat(connector): add GitHub OAuth support
fix(kakao): resolve client_secret optional handling
docs(contributing): add PR guidelines
```

### PR Description Template

```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix (non-breaking change)
- [ ] New feature (non-breaking change)
- [ ] Breaking change (fix or feature that would cause existing functionality to change)
- [ ] Documentation update

## Checklist
- [ ] My code follows the code style of this project
- [ ] I have added tests to cover my changes
- [ ] All new and existing tests passed
- [ ] I have updated the documentation accordingly
- [ ] I have added an entry to CHANGELOG.md (if applicable)

## Related Issues
Closes #(issue number)

## Screenshots (if applicable)

## Additional Notes
```

### Review Process

- At least one maintainer approval required
- CI/CD checks must pass
- No merge conflicts
- Documentation updated (if needed)

## Adding a New OAuth Provider

### Step-by-Step Guide

1. **Create new module**
   ```bash
   cd oauth-connector
   mkdir connector-github
   ```

2. **Implement required classes**

   **Config Class:**
   ```java
   public class OAuth2GitHubConfig extends OAuthBaseConfig {
       private String scope;

       @Override
       public String getAuthorizeEndpoint() {
           return "https://github.com/login/oauth/authorize";
       }

       @Override
       public String getAccessTokenEndpoint() {
           return "https://github.com/login/oauth/access_token";
       }
   }
   ```

   **Authorize Function:**
   ```java
   public class OAuth2GitHubGenerateAuthorizeEndpointFunction
       implements OAuth2GenerateAuthorizeEndpointFunction {

       @Override
       public String generate(State state) {
           // Implementation
       }
   }
   ```

   **Token Function:**
   ```java
   public class OAuth2GitHubAccesstokenFunction
       implements OAuth2AccessTokenEndpointFunction<OAuth2GitHubTokenRes> {

       @Override
       public OAuth2GitHubTokenRes issue(Verifier verifier, State state) {
           // Implementation
       }

       @Override
       public OAuth2GitHubTokenRes refresh(Token refreshToken) {
           // Implementation
       }
   }
   ```

   **Token Response:**
   ```java
   @Getter
   @Setter
   public class OAuth2GitHubTokenRes implements TokenPack {
       private String access_token;
       private String token_type;
       private String scope;
   }
   ```

3. **Add tests**
   ```java
   class OAuth2GitHubServiceTest {
       @Test
       void testGenerateAuthorizeUrl() {
           // Test implementation
       }
   }
   ```

4. **Update documentation**
   - Add to PRODUCT.md
   - Create connector-github/README.md
   - Add usage example to main README.md

5. **Add to parent pom.xml**
   ```xml
   <modules>
       <module>connector-github</module>
   </modules>
   ```

## Testing Guidelines

### Unit Tests

- **Location**: `src/test/java`
- **Naming**: `*Test.java` or `*Tests.java`
- **Framework**: JUnit 5 (Jupiter)

```java
@Test
void shouldGenerateValidAuthorizeUrl() {
    OAuth2NaverConfig config = OAuth2NaverConfig.builder()
        .clientId("test-client-id")
        .redirectUri("http://localhost/callback")
        .scope("profile,email")
        .build();

    OAuth2NaverGenerateAuthorizeEndpointFunction function =
        new OAuth2NaverGenerateAuthorizeEndpointFunction(config);

    State state = new RandomStringStateGenerator().generate("TEST");
    String url = function.generate(state);

    assertThat(url).contains("client_id=test-client-id");
    assertThat(url).contains("redirect_uri=");
    assertThat(url).contains("state=");
}
```

### Integration Tests

- Use `@Tag("integration")` for integration tests
- Mock external OAuth providers using MockWebServer
- Test real OAuth flow with test credentials (if available)

### Test Coverage

- Aim for 80%+ code coverage
- Focus on critical paths and edge cases
- Use JaCoCo for coverage reports

```bash
# Generate coverage report
mvn clean test jacoco:report
```

## Documentation

### Code Documentation

- **Javadoc**: Required for public APIs
- **Comments**: Explain "why", not "what"
- **README**: Update for significant changes

### Example Javadoc

```java
/**
 * Generates OAuth 2.0 authorization URL for Naver login.
 *
 * @param state CSRF protection state parameter
 * @return Authorization URL with all required parameters
 * @throws OAuthParamException if required configuration is missing
 * @see <a href="https://developers.naver.com/docs/login/api/">Naver Login API</a>
 */
public String generate(State state) {
    // Implementation
}
```

### Documentation Files

- **README.md**: Project overview and quick start
- **PRODUCT.md**: Detailed product documentation
- **CHANGELOG.md**: Version history
- **Module README**: Each module should have its own README

## Getting Help

- **GitHub Issues**: For bugs and feature requests
- **GitHub Discussions**: For questions and general discussion
- **Email**: archmagece@gmail.com for sensitive issues

## Recognition

Contributors will be recognized in:
- GitHub Contributors page
- CHANGELOG.md for significant contributions
- README.md (optional)

## License

By contributing, you agree that your contributions will be licensed under the Apache License 2.0.

---

Thank you for contributing to sb-oauth-java! 🎉
