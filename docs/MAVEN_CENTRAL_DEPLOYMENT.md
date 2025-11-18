# Maven Central Deployment Guide

This document provides a comprehensive guide for deploying sb-oauth-java to Maven Central (OSSRH).

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [One-Time Setup](#one-time-setup)
3. [Pre-Deployment Checklist](#pre-deployment-checklist)
4. [Deployment Process](#deployment-process)
5. [Post-Deployment](#post-deployment)
6. [Troubleshooting](#troubleshooting)

---

## Prerequisites

### Required Accounts
- [ ] Sonatype OSSRH account (https://issues.sonatype.org)
- [ ] GPG key pair for artifact signing
- [ ] GitHub repository with admin access

### Required Tools
- [ ] Java 21+ (for building)
- [ ] Maven 3.8+ (via wrapper: `./mvnw`)
- [ ] GPG 2.x (for signing)
- [ ] Git (for tagging)

---

## One-Time Setup

### 1. Create Sonatype OSSRH Account

1. Create JIRA account at https://issues.sonatype.org/
2. Create a new ticket requesting repository access:
   - Project: Community Support - Open Source Project Repository Hosting (OSSRH)
   - Issue Type: New Project
   - Group Id: `org.scriptonbasestar.oauth`
   - Project URL: `https://github.com/archmagece-backyard/sb-oauth-java`
   - SCM URL: `https://github.com/archmagece-backyard/sb-oauth-java.git`

3. Wait for Sonatype team approval (usually 1-2 business days)

**Status**: ✅ Complete (verify at https://s01.oss.sonatype.org/)

### 2. Generate GPG Key

```bash
# Generate new GPG key
gpg --full-generate-key

# Choose:
# - Key type: RSA and RSA
# - Key size: 4096 bits
# - Expiration: 2 years
# - Name: ScriptonBasestar Team
# - Email: archmagece@gmail.com

# List keys to get key ID
gpg --list-secret-keys --keyid-format LONG

# Output:
# sec   rsa4096/YOUR_KEY_ID 2025-01-01 [SC]
#       YOUR_FULL_KEY_FINGERPRINT
# uid   [ultimate] ScriptonBasestar Team <archmagece@gmail.com>

# Publish to key server
gpg --keyserver keys.openpgp.org --send-keys YOUR_KEY_ID

# Export private key for GitHub Secrets (base64 encoded)
gpg --export-secret-keys YOUR_KEY_ID | base64 > gpg-private-key.txt
```

**Status**: ⏳ Pending

### 3. Configure GitHub Secrets

Go to GitHub repository → Settings → Secrets and variables → Actions → New repository secret:

| Secret Name | Value | Description |
|-------------|-------|-------------|
| `OSSRH_USERNAME` | Your OSSRH username | Sonatype JIRA username |
| `OSSRH_PASSWORD` | Your OSSRH password | Sonatype JIRA password or token |
| `GPG_PRIVATE_KEY` | Content of gpg-private-key.txt | Base64-encoded GPG private key |
| `GPG_PASSPHRASE` | Your GPG passphrase | Passphrase for GPG key |

**Status**: ⏳ Pending

### 4. Verify POM Configuration

Check `pom.xml` contains all required metadata:

```xml
<project>
  <!-- Required for Maven Central -->
  <name>Scripton OAuth Library</name>
  <description>OAuth 2.0 client library for Java...</description>
  <url>https://github.com/ScriptonBasestar-io/sb-oauth-java</url>

  <licenses>
    <license>
      <name>Apache License, Version 2.0</name>
      <url>https://www.apache.org/licenses/LICENSE-2.0.txt</url>
    </license>
  </licenses>

  <developers>
    <developer>
      <id>archmagece</id>
      <name>ScriptonBasestar Team</name>
      <email>archmagece@gmail.com</email>
    </developer>
  </developers>

  <scm>
    <connection>scm:git:git://github.com/ScriptonBasestar-io/sb-oauth-java.git</connection>
    <developerConnection>scm:git:ssh://github.com:ScriptonBasestar-io/sb-oauth-java.git</developerConnection>
    <url>https://github.com/ScriptonBasestar-io/sb-oauth-java/tree/main</url>
  </scm>

  <distributionManagement>
    <snapshotRepository>
      <id>ossrh</id>
      <url>https://s01.oss.sonatype.org/content/repositories/snapshots</url>
    </snapshotRepository>
    <repository>
      <id>ossrh</id>
      <url>https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/</url>
    </repository>
  </distributionManagement>
</project>
```

**Status**: ✅ Complete

### 5. Verify Release Profile

Check `pom.xml` has release profile configured:

```xml
<profiles>
  <profile>
    <id>release</id>
    <build>
      <plugins>
        <!-- Source JAR -->
        <plugin>
          <groupId>org.apache.maven.plugins</groupId>
          <artifactId>maven-source-plugin</artifactId>
        </plugin>

        <!-- Javadoc JAR -->
        <plugin>
          <groupId>org.apache.maven.plugins</groupId>
          <artifactId>maven-javadoc-plugin</artifactId>
        </plugin>

        <!-- GPG Signing -->
        <plugin>
          <groupId>org.apache.maven.plugins</groupId>
          <artifactId>maven-gpg-plugin</artifactId>
        </plugin>

        <!-- Nexus Staging -->
        <plugin>
          <groupId>org.sonatype.plugins</groupId>
          <artifactId>nexus-staging-maven-plugin</artifactId>
        </plugin>
      </plugins>
    </build>
  </profile>
</profiles>
```

**Status**: ✅ Complete

---

## Pre-Deployment Checklist

Run this checklist before every release:

### Code Quality

- [ ] All tests passing: `mvn clean test`
- [ ] Code quality checks passing:
  - [ ] Checkstyle: `mvn checkstyle:check`
  - [ ] SpotBugs: `mvn spotbugs:check`
  - [ ] PMD: `mvn pmd:check`
- [ ] Test coverage meets minimum (50%): `mvn jacoco:check`
- [ ] No SNAPSHOT dependencies in pom.xml
- [ ] All TODO/FIXME comments reviewed

### Documentation

- [ ] CHANGELOG.md updated with release notes
- [ ] README.md version references updated
- [ ] All module READMEs reviewed and up-to-date
- [ ] Javadoc generates without errors: `mvn javadoc:aggregate`
- [ ] Example code tested and working
- [ ] Release notes prepared in `.github/release-notes/`

### Build

- [ ] Clean build successful: `mvn clean install`
- [ ] All modules build successfully
- [ ] Source JAR generated
- [ ] Javadoc JAR generated
- [ ] No warnings in build output (acceptable warnings documented)

### Legal & Compliance

- [ ] LICENSE file present in root
- [ ] All source files have license headers
- [ ] NOTICE file updated if required
- [ ] Third-party licenses reviewed
- [ ] No proprietary code or credentials committed

### Version Management

- [ ] Version number follows Semantic Versioning (MAJOR.MINOR.PATCH)
- [ ] Version updated in all pom.xml files (or using versions:set)
- [ ] Version tag format: `vX.Y.Z` (e.g., `v1.0.0`)
- [ ] Release branch created if needed

---

## Deployment Process

### Automated Deployment (Recommended)

Use GitHub Actions workflow for automated release:

```bash
# 1. Ensure all changes committed and pushed
git status
git push origin main

# 2. Create and push version tag
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0

# 3. GitHub Actions automatically:
#    - Builds project
#    - Runs tests
#    - Generates Javadoc
#    - Creates GitHub Release
#    - (Optional) Deploys to Maven Central when credentials configured
```

### Manual Deployment

If automated deployment is not available:

```bash
# 1. Set version
./mvnw versions:set -DnewVersion=1.0.0
./mvnw versions:commit

# 2. Clean build with all checks
./mvnw clean install

# 3. Deploy to OSSRH (staging)
./mvnw clean deploy -P release -DskipTests=false

# 4. Login to Nexus Repository Manager
# https://s01.oss.sonatype.org/

# 5. Verify staged repository
#    - Check artifacts uploaded
#    - Verify signatures
#    - Review POM metadata

# 6. Close staging repository
#    - Click "Close" button
#    - Wait for validation (2-5 minutes)

# 7. Release to Maven Central
#    - Click "Release" button
#    - Artifacts sync to Maven Central in ~10 minutes
#    - Full propagation takes 2-4 hours

# 8. Create Git tag
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0

# 9. Create GitHub Release
#    - Go to GitHub Releases
#    - Draft new release
#    - Select tag v1.0.0
#    - Copy content from .github/release-notes/v1.0.0.md
#    - Attach release artifacts
#    - Publish release
```

### Deployment Verification

After deployment, verify:

```bash
# 1. Check Maven Central (wait 10-30 minutes)
# https://central.sonatype.com/artifact/org.scriptonbasestar.oauth/sb-oauth-java/1.0.0

# 2. Test dependency resolution
mvn dependency:get \
  -Dartifact=org.scriptonbasestar.oauth:integration-spring-boot-starter:1.0.0

# 3. Verify PGP signatures
# Each artifact should have corresponding .asc signature file

# 4. Check Javadoc published
# https://javadoc.io/doc/org.scriptonbasestar.oauth/integration-spring-boot-starter/1.0.0
```

---

## Post-Deployment

### Immediate Actions

- [ ] Verify artifacts on Maven Central
- [ ] Update project README with new version
- [ ] Announce release on:
  - [ ] GitHub Discussions
  - [ ] Project blog/website
  - [ ] Social media (if applicable)
- [ ] Close milestone on GitHub (if applicable)
- [ ] Prepare for next development iteration

### Update Documentation

- [ ] Update version references in README.md
- [ ] Update version in CONTRIBUTING.md examples
- [ ] Update version in user guide examples
- [ ] Add link to release notes in documentation

### Monitoring

Monitor for the first week after release:
- [ ] Watch GitHub Issues for release-related problems
- [ ] Monitor Maven Central download statistics
- [ ] Check for user feedback and questions
- [ ] Review any security advisories

---

## Troubleshooting

### Common Issues

#### 1. GPG Signing Fails

**Error**: `gpg: signing failed: No secret key`

**Solution**:
```bash
# List available keys
gpg --list-secret-keys

# Set GPG_TTY environment variable
export GPG_TTY=$(tty)

# Or specify key explicitly
./mvnw deploy -P release -Dgpg.keyname=YOUR_KEY_ID
```

#### 2. OSSRH Authentication Fails

**Error**: `401 Unauthorized`

**Solution**:
- Verify credentials in ~/.m2/settings.xml
- Check OSSRH account is active
- Try using OSSRH token instead of password
- Generate token: https://s01.oss.sonatype.org/#profile;User%20Token

#### 3. Staging Repository Validation Fails

**Error**: Various validation errors in Nexus

**Common causes**:
- Missing POM metadata (name, description, url, license, developers, scm)
- Missing source JAR or Javadoc JAR
- Invalid PGP signatures
- SNAPSHOT dependencies in release

**Solution**:
Review Nexus activity log for specific errors and fix in POM.

#### 4. Central Sync Delayed

**Issue**: Artifacts not appearing on Maven Central after hours

**Solution**:
- Sync can take 2-4 hours (sometimes up to 24 hours)
- Check status: https://status.maven.org/
- Verify on https://repo1.maven.org/maven2/org/scriptonbasestar/oauth/
- If >24 hours, contact Sonatype support

#### 5. Javadoc Generation Fails

**Error**: Javadoc errors prevent release

**Solution**:
```bash
# Generate Javadoc to see errors
./mvnw javadoc:aggregate

# Fix common issues:
# - Missing @param/@return tags
# - Invalid HTML in comments
# - Broken @link references

# Temporarily allow errors (not recommended for release)
<doclint>none</doclint>
```

### Getting Help

- **Sonatype Support**: https://issues.sonatype.org/
- **Maven Central Guide**: https://central.sonatype.org/publish/publish-guide/
- **GPG Guide**: https://central.sonatype.org/publish/requirements/gpg/
- **Project Issues**: https://github.com/archmagece-backyard/sb-oauth-java/issues

---

## Quick Reference

### Essential Commands

```bash
# Set version
./mvnw versions:set -DnewVersion=1.0.0

# Build
./mvnw clean install

# Deploy to staging
./mvnw clean deploy -P release

# Rollback version
./mvnw versions:revert

# Check for SNAPSHOT dependencies
./mvnw versions:display-dependency-updates
```

### Essential URLs

- **OSSRH Nexus**: https://s01.oss.sonatype.org/
- **Maven Central Search**: https://central.sonatype.com/
- **Maven Central Repo**: https://repo1.maven.org/maven2/
- **PGP Key Server**: https://keys.openpgp.org/
- **Javadoc.io**: https://javadoc.io/

---

## Appendix: GitHub Actions Configuration

To enable Maven Central deployment in GitHub Actions, uncomment lines 90-111 in `.github/workflows/release.yml` after configuring secrets.

The workflow will automatically:
1. Build and test
2. Sign artifacts with GPG
3. Deploy to OSSRH
4. Create GitHub Release
5. Generate release summary

---

**Last Updated**: 2025-01-18
**Document Version**: 1.0.0
**Maintainer**: ScriptonBasestar Team
