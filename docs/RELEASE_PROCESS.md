# Release Process

Complete guide for creating and publishing releases of sb-oauth-java.

## Table of Contents

1. [Overview](#overview)
2. [Release Types](#release-types)
3. [Versioning Strategy](#versioning-strategy)
4. [Pre-Release Checklist](#pre-release-checklist)
5. [Release Process](#release-process)
6. [Post-Release Tasks](#post-release-tasks)
7. [Rollback Procedure](#rollback-procedure)

---

## Overview

sb-oauth-java uses **automated GitHub Actions** for releasing. The process:
1. Create and push a version tag
2. GitHub Actions automatically builds, tests, and creates release
3. (Optional) Deploy to Maven Central when credentials configured
4. Announce release

---

## Release Types

### Major Release (X.0.0)

**When to use**:
- Breaking API changes
- Major new features
- Java version upgrade (e.g., Java 17 → Java 21)
- Framework version upgrade (e.g., Spring Boot 2.x → 3.x)

**Example**: `1.0.0` → `2.0.0`

**Process**:
- Plan migration guide
- Update MIGRATION.md
- Announce well in advance
- Consider RC (Release Candidate) versions

### Minor Release (x.Y.0)

**When to use**:
- New OAuth provider support
- New features (backward compatible)
- New storage backend
- Significant enhancements

**Example**: `1.0.0` → `1.1.0`

**Process**:
- Standard release process
- Update feature documentation
- Highlight new capabilities

### Patch Release (x.y.Z)

**When to use**:
- Bug fixes
- Security patches
- Dependency updates
- Documentation fixes
- Performance improvements

**Example**: `1.0.0` → `1.0.1`

**Process**:
- Fast-track if security critical
- Minimal testing for low-risk changes
- Focus on regression testing

### Pre-Release Versions

**Alpha** (`1.0.0-alpha.1`):
- Early development
- Unstable API
- Internal testing

**Beta** (`1.0.0-beta.1`):
- Feature complete
- API stabilizing
- Public testing

**Release Candidate** (`1.0.0-RC1`):
- Production ready candidate
- Final testing
- Bug fixes only

---

## Versioning Strategy

We follow [Semantic Versioning 2.0.0](https://semver.org/):

```
MAJOR.MINOR.PATCH

Examples:
1.0.0       - First stable release
1.1.0       - Added Kakao OAuth support
1.1.1       - Fixed token refresh bug
2.0.0       - Java 21 required (breaking change)
1.2.0-beta.1 - Beta version for 1.2.0
```

### Version Compatibility

| Version Change | Binary Compatibility | Source Compatibility | Behavior Compatibility |
|----------------|---------------------|---------------------|------------------------|
| **Patch** (x.y.Z) | ✅ Yes | ✅ Yes | ✅ Yes |
| **Minor** (x.Y.0) | ✅ Yes | ✅ Yes | ⚠️ May change |
| **Major** (X.0.0) | ❌ No | ❌ No | ❌ No |

---

## Pre-Release Checklist

### 1. Code Quality

```bash
# Run full test suite
./mvnw clean test

# Run code quality checks
./mvnw checkstyle:check
./mvnw spotbugs:check
./mvnw pmd:check

# Verify coverage
./mvnw jacoco:check
```

**Required**:
- [ ] All tests passing
- [ ] No Checkstyle violations
- [ ] No critical SpotBugs issues
- [ ] Test coverage ≥ 50%

### 2. Documentation

- [ ] CHANGELOG.md updated with release changes
- [ ] README.md version references updated
- [ ] All module READMEs reviewed
- [ ] USER_GUIDE.md examples tested
- [ ] FAQ.md updated with new issues
- [ ] Release notes drafted in `.github/release-notes/vX.Y.Z.md`

### 3. Dependencies

```bash
# Check for dependency updates
./mvnw versions:display-dependency-updates

# Check for plugin updates
./mvnw versions:display-plugin-updates

# Verify no SNAPSHOT dependencies (for releases)
grep -r "SNAPSHOT" pom.xml
```

**Required**:
- [ ] No SNAPSHOT dependencies in release version
- [ ] Critical security updates applied
- [ ] Dependency versions documented in CHANGELOG

### 4. Build

```bash
# Clean build from scratch
./mvnw clean install

# Generate Javadoc
./mvnw javadoc:aggregate

# Create source JAR
./mvnw source:jar

# Verify artifacts
find . -name "*.jar" -ls
```

**Required**:
- [ ] Clean build successful
- [ ] Javadoc generates without errors
- [ ] Source JAR created
- [ ] All modules build successfully

### 5. Examples

- [ ] `spring-boot-basic` example runs successfully
- [ ] All example READMEs accurate
- [ ] Dependencies in examples match release version

### 6. Version Management

```bash
# Check current version
grep "<version>" pom.xml | head -1

# Update version (don't commit yet)
./mvnw versions:set -DnewVersion=1.0.0

# Verify changes
git diff pom.xml

# Commit will be done by release workflow
```

**Required**:
- [ ] Version number follows SemVer
- [ ] Version updated in all pom.xml files
- [ ] Version tag format: `vX.Y.Z`

---

## Release Process

### Automated Release (Recommended)

#### Option 1: Tag-Based Release

```bash
# 1. Ensure main branch is ready
git checkout main
git pull origin main

# 2. Run pre-release checks
./mvnw clean verify

# 3. Update CHANGELOG.md
# Add release date to [Unreleased] section
# Move changes to new [X.Y.Z] section

# 4. Commit changelog
git add CHANGELOG.md
git commit -m "docs: Prepare CHANGELOG for v1.0.0 release"
git push origin main

# 5. Create and push tag
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0

# 6. GitHub Actions automatically:
#    - Updates version in pom.xml
#    - Builds project
#    - Runs tests
#    - Generates Javadoc
#    - Creates GitHub Release
#    - (Optional) Deploys to Maven Central
```

#### Option 2: Manual Workflow Dispatch

```bash
# 1. Go to GitHub Actions
#    https://github.com/archmagece-backyard/sb-oauth-java/actions/workflows/release.yml

# 2. Click "Run workflow"

# 3. Select branch: main

# 4. Enter version: 1.0.0

# 5. Click "Run workflow"

# 6. Monitor workflow progress
```

### Manual Release (Fallback)

If automated release fails:

```bash
# 1. Set version
./mvnw versions:set -DnewVersion=1.0.0
./mvnw versions:commit

# 2. Build
./mvnw clean install

# 3. Commit version
git add pom.xml
git commit -m "build: Bump version to 1.0.0"
git push origin main

# 4. Create tag
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0

# 5. Create GitHub Release manually
#    - Go to https://github.com/archmagece-backyard/sb-oauth-java/releases/new
#    - Tag: v1.0.0
#    - Title: Release 1.0.0
#    - Body: Copy from .github/release-notes/v1.0.0.md
#    - Attach JARs from target/ directories
#    - Publish

# 6. (Optional) Deploy to Maven Central
./mvnw clean deploy -P release
```

---

## Post-Release Tasks

### Immediate (Day 0)

- [ ] **Verify GitHub Release**
  - Check release created successfully
  - Verify artifacts attached
  - Review release notes formatting

- [ ] **Update Documentation**
  - Update README.md badges
  - Update version in CONTRIBUTING.md examples
  - Update version in USER_GUIDE.md examples

- [ ] **Verify Maven Central** (if deployed)
  - Check artifacts appear (10-30 minutes)
  - Test dependency resolution
  - Verify PGP signatures

- [ ] **Create Next Version**
  ```bash
  # Prepare for next development iteration
  ./mvnw versions:set -DnewVersion=1.1.0-SNAPSHOT
  ./mvnw versions:commit
  git add pom.xml
  git commit -m "build: Prepare for v1.1.0 development"
  git push origin main
  ```

### Short Term (Week 1)

- [ ] **Announce Release**
  - GitHub Discussions post
  - Update project website (if applicable)
  - Social media announcement (if applicable)

- [ ] **Monitor Issues**
  - Watch for release-related bugs
  - Quick fix critical issues (patch release)
  - Update FAQ with common questions

- [ ] **Update Examples**
  - Verify examples work with new version
  - Create tutorial blog post (if major release)

### Long Term (Month 1)

- [ ] **Collect Feedback**
  - Monitor GitHub Issues
  - Track Maven Central downloads
  - Survey users (if applicable)

- [ ] **Plan Next Release**
  - Create milestone for next version
  - Prioritize features from feedback
  - Update roadmap in PRODUCT.md

---

## Rollback Procedure

If critical issues found after release:

### 1. Assess Severity

**Critical** (security, data loss):
- Immediate action required
- Yank release from Maven Central (contact Sonatype)
- Delete GitHub Release (or mark as pre-release)
- Issue patch release ASAP

**Major** (broken functionality):
- Issue patch release within 24-48 hours
- Document workarounds
- Keep release available but update notes

**Minor** (inconvenience):
- Fix in next regular release
- Document in errata
- No need to rollback

### 2. GitHub Release Rollback

```bash
# 1. Delete release on GitHub
#    Go to: https://github.com/archmagece-backyard/sb-oauth-java/releases
#    Click release → Delete

# 2. Delete tag locally and remotely
git tag -d v1.0.0
git push origin :refs/tags/v1.0.0

# 3. Fix issues in code

# 4. Create new patch release
git tag -a v1.0.1 -m "Release version 1.0.1 (fixes v1.0.0 issues)"
git push origin v1.0.1
```

### 3. Maven Central Rollback

**Important**: You cannot delete artifacts from Maven Central once released.

**Options**:
1. **Patch Release**: Issue v1.0.1 immediately
2. **Deprecation**: Mark as deprecated in metadata
3. **Contact Sonatype**: For critical security issues only

```bash
# Issue emergency patch
./mvnw versions:set -DnewVersion=1.0.1
# Fix critical issues
./mvnw clean deploy -P release
```

---

## Release Checklist Summary

Use this checklist for every release:

### Pre-Release
- [ ] All tests passing
- [ ] Code quality checks pass
- [ ] Documentation updated
- [ ] CHANGELOG.md updated
- [ ] Release notes prepared
- [ ] No SNAPSHOT dependencies
- [ ] Examples tested

### Release
- [ ] Version tagged (vX.Y.Z)
- [ ] GitHub Actions successful
- [ ] GitHub Release created
- [ ] Artifacts available
- [ ] (Optional) Maven Central deployed

### Post-Release
- [ ] GitHub Release verified
- [ ] Documentation updated
- [ ] Next version prepared (X.Y.Z-SNAPSHOT)
- [ ] Release announced
- [ ] Issues monitored

---

## Automation Details

### GitHub Actions Workflow

File: `.github/workflows/release.yml`

**Triggers**:
- Push to tag matching `v*` pattern
- Manual workflow dispatch

**Steps**:
1. Checkout code with full history
2. Setup JDK 21
3. Extract version from tag
4. Update version in pom.xml
5. Build project with tests
6. Generate Javadoc
7. Create release artifacts
8. Extract changelog for version
9. Create GitHub Release
10. (Optional) Deploy to Maven Central
11. Generate release summary

**Secrets Required** (for Maven Central):
- `OSSRH_USERNAME`
- `OSSRH_PASSWORD`
- `GPG_PRIVATE_KEY`
- `GPG_PASSPHRASE`

### Release Artifacts

Each release includes:
- Source code (ZIP, tar.gz)
- Compiled JARs for all modules
- Source JARs
- Javadoc JARs
- PGP signatures (.asc files)
- CHANGELOG.md
- README.md

---

## FAQ

**Q: Can I release from a branch other than main?**
A: Not recommended. Always release from main to ensure stability.

**Q: What if the build fails during release?**
A: Delete the tag, fix the issue, and re-tag.

**Q: How do I create a hotfix release?**
A: Checkout the release tag, apply fix, create new patch version tag.

**Q: Can I skip tests during release?**
A: Never skip tests in release builds. Tests ensure quality.

**Q: How long until artifacts appear on Maven Central?**
A: 10-30 minutes to appear, 2-4 hours for full propagation.

---

## Resources

- [Semantic Versioning](https://semver.org/)
- [Keep a Changelog](https://keepachangelog.com/)
- [Maven Central Guide](https://central.sonatype.org/publish/publish-guide/)
- [GitHub Releases](https://docs.github.com/en/repositories/releasing-projects-on-github)

---

**Last Updated**: 2025-01-18
**Document Version**: 1.0.0
**Maintainer**: ScriptonBasestar Team
