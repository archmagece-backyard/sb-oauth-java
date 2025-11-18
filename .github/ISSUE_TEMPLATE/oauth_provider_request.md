---
name: OAuth Provider Request
about: Request support for a new OAuth provider
title: '[PROVIDER] Add support for '
labels: enhancement, new-provider
assignees: ''
---

## OAuth Provider Information

**Provider Name:** <!-- e.g., Apple, GitHub, LINE -->

**Official Documentation:** <!-- Link to OAuth documentation -->

**Registration URL:** <!-- Where to register OAuth applications -->

## Use Case

**Why is this provider needed?**
<!-- Describe the use case and target users -->

**Target Region/Market:**
<!-- e.g., Global, Korea, Japan -->

**Priority:**
- [ ] High (Required for production)
- [ ] Medium (Nice to have)
- [ ] Low (Future consideration)

## OAuth 2.0 Specifications

**Grant Types Supported:**
- [ ] Authorization Code
- [ ] Refresh Token
- [ ] Client Credentials
- [ ] Other: ___________

**OIDC Support:**
- [ ] Yes
- [ ] No
- [ ] Unknown

**Unique Features or Requirements:**
<!-- e.g., JWT-based client_secret, special headers, unique parameters -->

-
-

## API Endpoints

**Authorization Endpoint:**
```
https://...
```

**Token Endpoint:**
```
https://...
```

**User Info Endpoint:**
```
https://...
```

## Authentication Requirements

**Client Credentials:**
- [ ] client_id (required)
- [ ] client_secret (required/optional)
- [ ] Other: ___________

**Additional Parameters:**
<!-- e.g., tenant_id, team_id, api_key -->

-
-

## Scope Examples

**Common Scopes:**
```
profile, email, ...
```

## Token Response Format

**Token Type:**
- [ ] Bearer
- [ ] JWT
- [ ] Other: ___________

**Response Format:**
- [ ] JSON
- [ ] URL-encoded
- [ ] Other: ___________

**Example Response:**
```json
{
  "access_token": "...",
  "token_type": "Bearer",
  "expires_in": 3600,
  "refresh_token": "..."
}
```

## Special Considerations

**Known Issues or Quirks:**
<!-- e.g., Naver's non-rotating refresh token, Apple's JWT client_secret -->

-
-

**Security Requirements:**
<!-- e.g., PKCE required, specific redirect URI restrictions -->

-
-

## Implementation Willingness

- [ ] I can provide OAuth credentials for testing
- [ ] I can help implement this provider
- [ ] I can help with testing
- [ ] I can contribute documentation

## Similar Providers

<!-- Are there similar providers already supported? -->

This provider is similar to: <!-- e.g., Google (OIDC), Facebook (Graph API) -->

## Additional Resources

<!-- Links to SDK, example code, community discussions, etc. -->

-
-
