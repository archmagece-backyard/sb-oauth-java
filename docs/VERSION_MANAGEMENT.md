# Version Management Strategy

Comprehensive guide for version management, branching, and release strategies for sb-oauth-java.

## Table of Contents

1. [Versioning Scheme](#versioning-scheme)
2. [Branch Strategy](#branch-strategy)
3. [Tag Management](#tag-management)
4. [Dependency Versioning](#dependency-versioning)
5. [Backward Compatibility](#backward-compatibility)
6. [Deprecation Policy](#deprecation-policy)

---

## Versioning Scheme

### Semantic Versioning 2.0.0

We strictly follow [Semantic Versioning](https://semver.org/):

```
MAJOR.MINOR.PATCH[-PRERELEASE][+BUILD]

Examples:
1.0.0              - First stable release
1.1.0              - New feature (backward compatible)
1.1.1              - Bug fix
2.0.0              - Breaking change
1.2.0-beta.1       - Beta version
1.2.0-RC1          - Release candidate
1.2.0+20250118     - Build metadata
```

### Version Components

#### MAJOR Version (X.0.0)

Increment when making **incompatible API changes**.

**Triggers**:
- Breaking API changes (method signature changes, removed methods)
- Removal of deprecated features
- Major dependency upgrades with breaking changes
  - Java version upgrade (11 → 17 → 21)
  - Spring Boot major version (2.x → 3.x)
  - Framework changes (Spring Security OAuth → sb-oauth-java)
- Architecture changes affecting compatibility

**Examples**:
```java
// v1.x.x
public void authenticate(String clientId, String secret) { }

// v2.0.0 - BREAKING: Parameter order changed
public void authenticate(String secret, String clientId) { }
```

**Migration Path**:
- Provide migration guide in MIGRATION.md
- Deprecated features removed after 1+ major versions
- Consider providing compatibility shim for critical changes

#### MINOR Version (x.Y.0)

Increment when adding **new functionality** in a backward compatible manner.

**Triggers**:
- New OAuth provider support (Apple, GitHub, etc.)
- New storage backend (MongoDB, PostgreSQL)
- New features/methods added
- Significant enhancements to existing features
- Non-breaking dependency updates

**Examples**:
```java
// v1.0.0
interface OAuth2Provider {
    OAuth2Token getToken(String code);
}

// v1.1.0 - COMPATIBLE: New method with default implementation
interface OAuth2Provider {
    OAuth2Token getToken(String code);

    // New feature
    default OAuth2Token refreshToken(String refreshToken) {
        throw new UnsupportedOperationException();
    }
}
```

**Backward Compatibility**:
- Existing code continues to work
- New features opt-in
- Default implementations for new interface methods
- No changes to existing method signatures

#### PATCH Version (x.y.Z)

Increment when making **backward compatible bug fixes**.

**Triggers**:
- Bug fixes
- Security patches
- Performance improvements
- Documentation fixes
- Internal refactoring (no API changes)
- Dependency patch updates

**Examples**:
```java
// v1.0.0 - Bug: NullPointerException
public String getUsername() {
    return user.getName(); // NPE if user is null
}

// v1.0.1 - Fix
public String getUsername() {
    return user != null ? user.getName() : "anonymous";
}
```

**Guarantee**:
- Drop-in replacement
- No behavior changes (except bugs fixed)
- No new features
- No deprecations

#### Pre-Release Versions

**Alpha** (`1.2.0-alpha.1`):
- Very early development
- Unstable API
- Internal testing only
- Major changes expected

**Beta** (`1.2.0-beta.1`):
- Feature complete for the release
- API stabilizing
- Public testing
- Bug fixes and small adjustments only

**Release Candidate** (`1.2.0-RC1`):
- Production-ready candidate
- Final testing phase
- Bug fixes only
- No new features
- If no issues found, becomes final release

**Ordering**:
```
1.0.0-alpha.1 < 1.0.0-alpha.2 < 1.0.0-beta.1 < 1.0.0-beta.2
< 1.0.0-RC1 < 1.0.0-RC2 < 1.0.0 < 1.0.1
```

#### Build Metadata

Build metadata for CI/CD and internal tracking:

```
1.2.0+20250118.commit-abc123
1.2.0+build.123
```

**Use cases**:
- CI/CD pipeline tracking
- Nightly builds
- Internal releases
- Not used for precedence in version ordering

---

## Branch Strategy

### Branch Types

#### Main Branch (`main`)

**Purpose**: Production-ready code

**Rules**:
- Always stable and deployable
- All tests must pass
- Code quality checks must pass
- Protected branch (requires PR + review)
- Direct commits not allowed

**Releases from**: All releases (major, minor, patch) are tagged from `main`

#### Development Branch (`develop`) - Optional

Currently not used. We use **trunk-based development** with feature branches.

#### Feature Branches (`feature/*`)

**Purpose**: Develop new features

**Naming**: `feature/issue-123-add-apple-oauth` or `feature/apple-oauth-support`

**Lifecycle**:
```bash
# Create from main
git checkout main
git pull origin main
git checkout -b feature/add-apple-oauth

# Develop
git add .
git commit -m "feat: Add Apple Sign In support"

# Keep up to date with main
git checkout main
git pull origin main
git checkout feature/add-apple-oauth
git rebase main

# Merge to main via PR
# After review and approval
```

**Merge Strategy**: Squash and merge (creates clean history)

#### Bugfix Branches (`bugfix/*`)

**Purpose**: Fix bugs in current development

**Naming**: `bugfix/issue-456-fix-token-refresh` or `bugfix/token-refresh-npe`

**Process**: Same as feature branches

#### Hotfix Branches (`hotfix/*`)

**Purpose**: Emergency fixes for production

**Naming**: `hotfix/v1.0.1-security-patch` or `hotfix/critical-npe`

**Lifecycle**:
```bash
# Create from release tag or main
git checkout -b hotfix/v1.0.1-security-patch v1.0.0

# Fix critical issue
git add .
git commit -m "fix: Patch critical security vulnerability"

# Create new patch version
./mvnw versions:set -DnewVersion=1.0.1

# Tag and release
git tag -a v1.0.1 -m "Release v1.0.1 - Security patch"
git push origin v1.0.1

# Merge back to main
git checkout main
git merge hotfix/v1.0.1-security-patch
git push origin main
```

#### Release Branches (`release/*`) - Not Currently Used

We use direct tagging from `main` instead of release branches.

**If needed in future**:
```bash
git checkout -b release/v1.2.0 main
# Final testing and version bump
git tag -a v1.2.0 -m "Release v1.2.0"
```

### Branch Protection Rules

**For `main` branch**:
- ✅ Require pull request before merging
- ✅ Require approvals: 1 reviewer
- ✅ Dismiss stale reviews
- ✅ Require status checks to pass:
  - CI/CD build
  - All tests
  - Code quality checks
- ✅ Require branches to be up to date
- ✅ Require linear history (squash merge)
- ✅ No force pushes
- ✅ No deletions

**For feature/* branches**:
- No special protection
- Delete after merge

---

## Tag Management

### Tag Naming Convention

```
v<MAJOR>.<MINOR>.<PATCH>[-<PRERELEASE>]

Examples:
v1.0.0
v1.1.0
v1.1.1
v2.0.0-beta.1
v2.0.0-RC1
```

**Rules**:
- Always prefix with `v`
- Use annotated tags (not lightweight)
- Tag message should match release notes
- Tags are immutable (never force push tags)

### Creating Tags

```bash
# Annotated tag (recommended)
git tag -a v1.0.0 -m "Release version 1.0.0

Major features:
- Spring Boot Auto-Configuration Starter
- Comprehensive documentation
- Code quality automation

See CHANGELOG.md for full details."

# Verify tag
git show v1.0.0

# Push tag
git push origin v1.0.0

# Push all tags (use with caution)
git push origin --tags
```

### Tag Organization

**Production releases**:
```
v1.0.0
v1.0.1
v1.1.0
v2.0.0
```

**Pre-releases**:
```
v1.1.0-alpha.1
v1.1.0-alpha.2
v1.1.0-beta.1
v1.1.0-RC1
```

### Tag Deletion (Emergency Only)

```bash
# Delete local tag
git tag -d v1.0.0

# Delete remote tag
git push origin :refs/tags/v1.0.0

# Re-create tag (only if not deployed to Maven Central)
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0
```

**WARNING**: Never delete tags that have been deployed to Maven Central.

---

## Dependency Versioning

### Direct Dependencies

**Update Strategy**:
- **Patch updates**: Apply automatically (via Dependabot)
- **Minor updates**: Review and test, apply quarterly
- **Major updates**: Plan carefully, may require own major version bump

**Examples**:

| Dependency | Current | Update Available | Action |
|------------|---------|------------------|--------|
| Jackson | 2.18.2 | 2.18.3 | Auto-apply (patch) |
| JUnit | 5.11.4 | 5.12.0 | Review quarterly (minor) |
| Spring Boot | 3.4.1 | 4.0.0 | Plan upgrade (major) |

### Transitive Dependencies

**Managed in parent POM** via `dependencyManagement`:
- Explicit versions for all transitive dependencies
- Overrides from dependency BOMs when needed
- Regular security scans via OWASP Dependency Check

### Version Ranges (Not Used)

We **do not use** version ranges:
```xml
<!-- ❌ Bad - Don't use version ranges -->
<version>[1.0.0,2.0.0)</version>
<version>1.+</version>

<!-- ✅ Good - Use exact versions -->
<version>1.2.3</version>
```

**Reason**: Reproducible builds require exact versions.

### SNAPSHOT Dependencies

**Development Only**:
```xml
<!-- Only during active development -->
<version>1.1.0-SNAPSHOT</version>
```

**Never in releases**:
- Release versions must have fixed dependencies
- No `-SNAPSHOT` suffixes in release builds
- Enforced by Maven Release Plugin and CI checks

---

## Backward Compatibility

### API Stability Levels

| Component | Stability | Changes Allowed |
|-----------|-----------|-----------------|
| **Public API** | Stable | Additions only (minor), deprecate before removal |
| **SPI/Extension Points** | Stable | Additions only (minor), deprecate before removal |
| **Internal APIs** | Unstable | Any changes allowed (even in patch) |
| **Configuration** | Stable | Additions only, keep old keys working |
| **Dependencies** | Mixed | Major updates = our major version |

### Public API Definition

**Includes**:
- All public classes and interfaces in published modules
- Configuration properties
- Maven artifacts (groupId, artifactId)
- Documented extension points

**Excludes** (internal):
- Classes in `*.internal.*` packages
- Classes annotated with `@Internal`
- Private/package-private members
- Test utilities

### Compatibility Matrix

| Our Version Change | Minimum Compatible Version | Maximum Compatible Version |
|-------------------|---------------------------|---------------------------|
| **Patch** (1.0.0 → 1.0.1) | 1.0.0 | 1.x.x |
| **Minor** (1.0.0 → 1.1.0) | 1.0.0 | 1.x.x |
| **Major** (1.x.x → 2.0.0) | 2.0.0 | 2.x.x |

**Example**:
```xml
<!-- App using 1.0.0 can upgrade to any 1.x.x -->
<dependency>
    <groupId>org.scriptonbasestar.oauth</groupId>
    <artifactId>integration-spring-boot-starter</artifactId>
    <version>1.2.3</version> <!-- Compatible with 1.0.0 code -->
</dependency>
```

---

## Deprecation Policy

### Deprecation Process

1. **Mark as Deprecated** (Version N)
   ```java
   /**
    * @deprecated Use {@link #newMethod()} instead. Will be removed in 3.0.0.
    */
   @Deprecated(since = "2.1.0", forRemoval = true)
   public void oldMethod() {
       // Still functional, delegates to new method
       newMethod();
   }

   public void newMethod() {
       // New implementation
   }
   ```

2. **Update Documentation** (Version N)
   - Add deprecation notice in Javadoc
   - Update migration guide
   - Add to CHANGELOG under "Deprecated"

3. **Minimum Deprecation Period**:
   - **Minor version deprecations**: Remove after 1 major version
   - **Major version deprecations**: Remove after 2 major versions
   - **Minimum time**: 6 months between deprecation and removal

4. **Removal** (Version N+1 or N+2)
   - Remove in next major version (at earliest)
   - Document in CHANGELOG under "Removed"
   - Provide migration path in MIGRATION.md

### Deprecation Examples

**Timeline**:
```
v2.1.0: Feature X deprecated, replacement Y added
v2.2.0: Feature X still available but deprecated
v2.3.0: Feature X still available but deprecated
v3.0.0: Feature X can be removed (minimum 1 major version passed)
```

**Best Practices**:
- Always provide replacement before deprecating
- Give users time to migrate
- Announce in release notes
- Consider compatibility layer if widely used

---

## Version Lifecycle

### Development Phase

```
main (1.0.0-SNAPSHOT)
  ↓
feature branches
  ↓
main (1.0.0-SNAPSHOT, updated)
  ↓
tag v1.0.0-beta.1
  ↓
testing
  ↓
tag v1.0.0-RC1
  ↓
final testing
  ↓
tag v1.0.0
  ↓
main (1.1.0-SNAPSHOT)
```

### Support Windows

| Version | Release Date | Active Support | Security Support | EOL |
|---------|-------------|---------------|------------------|-----|
| 2.x     | TBD         | 2 years       | +1 year          | TBD |
| 1.x     | 2025-01     | 1 year        | +1 year          | 2027-01 |

**Support Levels**:
- **Active Support**: Bug fixes, new features
- **Security Support**: Security patches only
- **EOL**: No updates

---

## Quick Reference

### Version Decision Tree

```
Breaking changes?
  YES → MAJOR version (2.0.0)
  NO  ↓
New features/providers?
  YES → MINOR version (1.1.0)
  NO  ↓
Bug fixes/patches?
  YES → PATCH version (1.0.1)
```

### Common Scenarios

| Scenario | Version Change | Example |
|----------|---------------|---------|
| Add Apple OAuth support | MINOR | 1.0.0 → 1.1.0 |
| Fix token refresh bug | PATCH | 1.0.0 → 1.0.1 |
| Upgrade to Java 25 | MAJOR | 1.x.x → 2.0.0 |
| Security patch | PATCH | 1.0.0 → 1.0.1 |
| Deprecate method | MINOR | 1.0.0 → 1.1.0 |
| Remove deprecated method | MAJOR | 1.x.x → 2.0.0 |
| Update documentation | PATCH | 1.0.0 → 1.0.1 |
| Performance improvement | PATCH | 1.0.0 → 1.0.1 |

---

## References

- [Semantic Versioning 2.0.0](https://semver.org/)
- [Calendar Versioning](https://calver.org/) (not used, for reference)
- [Git Flow](https://nvie.com/posts/a-successful-git-branching-model/)
- [Trunk Based Development](https://trunkbaseddevelopment.com/)

---

**Last Updated**: 2025-01-18
**Document Version**: 1.0.0
**Maintainer**: ScriptonBasestar Team
