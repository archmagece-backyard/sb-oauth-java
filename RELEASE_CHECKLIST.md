# Release Checklist

This checklist ensures a smooth release process for sb-oauth-java.

## Pre-Release (2-3 weeks before)

### Code Quality
- [ ] All tests passing (`mvn clean test`)
- [ ] Code review completed
- [ ] No critical bugs in issue tracker
- [ ] Security vulnerabilities addressed (OWASP Dependency Check)
- [ ] Code coverage > 80% (target for v2.0.0)

### Documentation
- [ ] CHANGELOG.md updated with release notes
- [ ] README.md version updated
- [ ] Javadoc generated without errors (`mvn javadoc:aggregate`)
- [ ] Migration guide updated (if breaking changes)
- [ ] API documentation complete

### Dependencies
- [ ] All dependencies up-to-date
- [ ] No SNAPSHOT dependencies
- [ ] Dependabot PRs reviewed and merged
- [ ] License compatibility verified

## Release Preparation (1 week before)

### Version Management
- [ ] Update version in pom.xml (remove -DEV suffix)
  ```bash
  mvn versions:set -DnewVersion=2.0.0
  mvn versions:commit
  ```
- [ ] Update version in README.md examples
- [ ] Update version in CHANGELOG.md

### Build Verification
- [ ] Clean build succeeds
  ```bash
  mvn clean install
  ```
- [ ] Release profile build succeeds
  ```bash
  mvn clean install -P release -DskipTests=false
  ```
- [ ] Sources JAR generated
- [ ] Javadoc JAR generated
- [ ] All modules build successfully

### Maven Central Prerequisites
- [ ] Sonatype OSSRH account created
- [ ] Group ID verified (org.scriptonbasestar.oauth)
- [ ] GPG key generated and uploaded
  ```bash
  gpg --gen-key
  gpg --keyserver keyserver.ubuntu.com --send-keys <KEY_ID>
  ```
- [ ] GitHub Secrets configured:
  - `OSSRH_USERNAME`
  - `OSSRH_PASSWORD`
  - `GPG_PRIVATE_KEY` (base64 encoded)
  - `GPG_PASSPHRASE`

## Release Day

### Create Release
- [ ] Create git tag
  ```bash
  git tag -a v2.0.0 -m "Release 2.0.0"
  git push origin v2.0.0
  ```
- [ ] GitHub Actions release workflow triggered
- [ ] Release created on GitHub
- [ ] Artifacts uploaded to GitHub Release

### Maven Central Deployment (Manual - First Time)
- [ ] Deploy to staging repository
  ```bash
  mvn clean deploy -P release
  ```
- [ ] Login to https://s01.oss.sonatype.org/
- [ ] Close staging repository
- [ ] Release staging repository
- [ ] Wait for sync to Maven Central (2-4 hours)
- [ ] Verify on https://search.maven.org/

### Verification
- [ ] GitHub Release page looks correct
- [ ] Download and test release artifacts
- [ ] Maven Central search shows new version
- [ ] Dependencies resolved correctly
  ```bash
  mvn dependency:get -Dartifact=org.scriptonbasestar.oauth:oauth-client:2.0.0
  ```

## Post-Release

### Documentation
- [ ] Update website/docs (if applicable)
- [ ] Blog post or announcement
- [ ] Update samples/examples repository

### Communication
- [ ] GitHub Release notes published
- [ ] Tweet/social media announcement (optional)
- [ ] Notify users on mailing list/Discord (if applicable)

### Preparation for Next Version
- [ ] Create next SNAPSHOT version
  ```bash
  mvn versions:set -DnewVersion=2.1.0-SNAPSHOT
  mvn versions:commit
  ```
- [ ] Push version bump to main branch
- [ ] Create milestone for next release
- [ ] Update roadmap in PRODUCT.md

## Rollback Procedure

If critical issues are discovered:

1. **Do NOT delete Maven Central release** (immutable)
2. Create hotfix version (e.g., 2.0.1)
3. Update CHANGELOG.md with fix details
4. Follow release process for patch version
5. Announce deprecation of problematic version

## Emergency Hotfix Release

For critical security issues:

1. Create hotfix branch from release tag
2. Apply minimal fix
3. Fast-track testing
4. Release as patch version (e.g., 2.0.1)
5. Skip non-critical checks
6. Deploy within 24 hours

## Tools and Commands

### Version Management
```bash
# Set version
mvn versions:set -DnewVersion=2.0.0
mvn versions:commit

# Revert if needed
mvn versions:revert
```

### Build
```bash
# Clean build
mvn clean install

# Release build (with signing)
mvn clean install -P release

# Skip tests (not recommended)
mvn clean install -DskipTests=true
```

### Release
```bash
# Deploy to Maven Central
mvn clean deploy -P release

# Create tag
git tag -a v2.0.0 -m "Release 2.0.0"
git push origin v2.0.0

# GitHub release (via web UI or CLI)
gh release create v2.0.0 --title "Release 2.0.0" --notes "See CHANGELOG.md"
```

## Troubleshooting

### Build Fails
- Check Java version (21 required)
- Update Maven (3.9.x+ required)
- Clear local repository: `rm -rf ~/.m2/repository/org/scriptonbasestar`

### GPG Signing Fails
- Verify GPG key: `gpg --list-keys`
- Check passphrase in secrets
- Use pinentry-mode loopback

### Maven Central Upload Fails
- Verify credentials in ~/.m2/settings.xml
- Check staging repository status
- Contact Sonatype support if needed

## References

- [Maven Central Publishing Guide](https://central.sonatype.org/publish/publish-guide/)
- [GPG Key Generation](https://central.sonatype.org/publish/requirements/gpg/)
- [Semantic Versioning](https://semver.org/)
- [Keep a Changelog](https://keepachangelog.com/)
