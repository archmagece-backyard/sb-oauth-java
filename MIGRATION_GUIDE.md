# 마이그레이션 가이드 v1.x → v2.0

이 문서는 sb-oauth-java를 v1.x에서 v2.0으로 업그레이드하는 방법을 설명합니다.

## 주요 변경사항 요약

### 1. Java 버전 요구사항
- **이전**: Java 8+
- **현재**: Java 17+ (권장: Java 21 LTS)

### 2. HttpClient 업그레이드
- **이전**: Apache HttpClient 4.x
- **현재**: Apache HttpClient 5.x

### 3. Spring Boot 버전
- **이전**: Spring Boot 2.0.5
- **현재**: Spring Boot 3.4.1 (선택사항)

### 4. 주요 라이브러리 업데이트

| 라이브러리 | 이전 버전 | 현재 버전 |
|-----------|-----------|-----------|
| Jackson | 2.9.6 | 2.18.2 |
| Guava | 26.0-jre | 33.4.0-jre |
| Groovy | codehaus | Apache 4.0.24 |
| JUnit | 4.x | 5.11.4 (Jupiter) |
| Lombok | (Spring Boot 관리) | 1.18.36 |

## 마이그레이션 단계

### Step 1: Java 버전 업그레이드

#### Maven 설정 업데이트

```xml
<!-- 이전 -->
<properties>
    <java_version>1.8</java_version>
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>
</properties>

<!-- 현재 -->
<properties>
    <java_version>21</java_version>
    <maven.compiler.source>21</maven.compiler.source>
    <maven.compiler.target>21</maven.compiler.target>
</properties>
```

#### 코드 변경사항

Java 17+에서 추가된 기능을 활용할 수 있습니다:

```java
// 이전: String concatenation
String authUrl = "https://example.com/oauth?client_id=" + clientId +
                 "&redirect_uri=" + redirectUri;

// 현재: Text Blocks (Java 15+)
String authUrl = """
    https://example.com/oauth?\
    client_id=%s&\
    redirect_uri=%s\
    """.formatted(clientId, redirectUri);
```

### Step 2: HttpClient 마이그레이션

HttpClient 4.x → 5.x로의 변경은 **내부적으로 처리**되므로, API 사용자는 변경할 필요가 없습니다.

만약 HttpClient를 직접 사용하는 경우:

```java
// 이전 (HttpClient 4.x)
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

CloseableHttpClient client = HttpClients.createDefault();
HttpGet request = new HttpGet(url);

// 현재 (HttpClient 5.x)
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;

CloseableHttpClient client = HttpClients.createDefault();
HttpGet request = new HttpGet(url);
```

### Step 3: 테스트 프레임워크 업그레이드 (JUnit 4 → 5)

```java
// 이전 (JUnit 4)
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

public class OAuth2Test {
    @Before
    public void setUp() { }

    @Test
    public void testAuthorization() {
        assertEquals(expected, actual);
    }
}

// 현재 (JUnit 5)
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class OAuth2Test {
    @BeforeEach
    void setUp() { }

    @Test
    void testAuthorization() {
        assertEquals(expected, actual);
    }
}
```

### Step 4: Spring Boot 3.x 마이그레이션 (선택사항)

Spring Boot를 사용하는 경우:

```xml
<!-- 이전 -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.0.5.RELEASE</version>
</parent>

<!-- 현재 -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.4.1</version>
</parent>
```

**주의**: Spring Boot 3.x는 Java 17 최소 요구

### Step 5: 의존성 버전 명시

v2.0부터는 모든 의존성 버전을 명시적으로 관리합니다:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
            <version>2.11.0</version>
        </dependency>
        <dependency>
            <groupId>org.apache.httpcomponents.client5</groupId>
            <artifactId>httpclient5</artifactId>
            <version>5.4.1</version>
        </dependency>
    </dependencies>
</dependencyManagement>
```

## 호환성 문제 해결

### 문제 1: `javax` 패키지를 찾을 수 없음

sb-oauth-java v2.0은 `javax` 패키지를 사용하지 않으므로 문제가 발생하지 않습니다.
만약 Spring Boot 3.x로 업그레이드하는 경우:

```xml
<!-- Jakarta EE 의존성 추가 -->
<dependency>
    <groupId>jakarta.servlet</groupId>
    <artifactId>jakarta.servlet-api</artifactId>
    <version>6.0.0</version>
</dependency>
```

### 문제 2: Groovy 컴파일 오류

```xml
<!-- 이전 (deprecated) -->
<plugin>
    <groupId>org.codehaus.gmaven</groupId>
    <artifactId>gmaven-plugin</artifactId>
    <version>1.5</version>
</plugin>

<!-- 현재 -->
<plugin>
    <groupId>org.codehaus.gmavenplus</groupId>
    <artifactId>gmavenplus-plugin</artifactId>
    <version>4.0.1</version>
</plugin>
```

### 문제 3: 보안 취약점 경고

v1.x에서 사용하던 일부 라이브러리는 보안 취약점이 있습니다:

| 라이브러리 | 취약점 | 해결 방법 |
|-----------|--------|-----------|
| XStream 1.4.10 | CVE-2020-26217 (RCE) | v2.0에서 제거됨 |
| Jackson 2.9.6 | CVE-2018-12022 | 2.18.2로 업그레이드 |
| Spring Boot 2.0.5 | 다수의 CVE | 3.4.1로 업그레이드 권장 |

## 빌드 및 테스트

### Maven으로 빌드

```bash
# Java 버전 확인
java -version  # Java 17 or 21 이상

# 프로젝트 빌드
mvn clean install

# 테스트 실행
mvn test

# 의존성 보안 검사
mvn org.owasp:dependency-check-maven:check
```

### Gradle로 빌드

```bash
./gradlew clean build
./gradlew test
```

## CI/CD 파이프라인

v2.0은 GitHub Actions CI/CD를 포함합니다:

- **자동 빌드**: Java 17 & 21 매트릭스 빌드
- **보안 스캔**: OWASP Dependency Check (주간 실행)
- **Dependabot**: 자동 의존성 업데이트

## 추가 리소스

- [OAuth 2.0 RFC 6749](https://tools.ietf.org/html/rfc6749)
- [Apache HttpClient 5.x Migration Guide](https://hc.apache.org/httpcomponents-client-5.0.x/migration-guide/)
- [Spring Boot 3.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)

## 도움이 필요하신가요?

- GitHub Issues: https://github.com/ScriptonBasestar-io/sb-oauth-java/issues
- 이메일: [maintainer email]

## 체크리스트

마이그레이션을 완료하기 전에 다음 사항을 확인하세요:

- [ ] Java 17 이상으로 업그레이드
- [ ] Maven/Gradle 의존성 업데이트
- [ ] JUnit 4 → JUnit 5 마이그레이션 (테스트 코드 존재 시)
- [ ] HttpClient 직접 사용 코드 확인 (있는 경우)
- [ ] 빌드 성공 확인
- [ ] 모든 테스트 통과 확인
- [ ] 보안 취약점 스캔 통과
