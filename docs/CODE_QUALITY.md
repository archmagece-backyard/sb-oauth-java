# 코드 품질 가이드

sb-oauth-java 프로젝트의 코드 품질 도구 및 표준에 대한 가이드입니다.

## 목차

1. [개요](#개요)
2. [사용하는 도구](#사용하는-도구)
3. [로컬에서 실행](#로컬에서-실행)
4. [CI/CD 통합](#cicd-통합)
5. [Pre-commit Hook](#pre-commit-hook)
6. [코드 품질 기준](#코드-품질-기준)

---

## 개요

프로젝트는 다음 도구를 사용하여 코드 품질을 유지합니다:

- **Checkstyle**: 코드 스타일 및 규칙 준수
- **SpotBugs**: 잠재적 버그 탐지
- **PMD**: 코드 냄새 및 안티패턴 탐지
- **JaCoCo**: 테스트 커버리지 측정

## 사용하는 도구

### 1. Checkstyle

**목적**: Java 코드 스타일 검사

**설정 파일**: `checkstyle.xml`

**주요 규칙**:
- 줄 길이: 최대 120자
- 들여쓰기: 탭 대신 스페이스
- 명명 규칙: camelCase, PascalCase
- Javadoc: public/protected 메서드 필수

**실행**:
```bash
# 검사
mvn checkstyle:check

# 리포트 생성
mvn checkstyle:checkstyle

# 리포트 위치
target/checkstyle-result.xml
target/site/checkstyle.html
```

### 2. SpotBugs

**목적**: 잠재적 버그 탐지

**주요 검사 항목**:
- Null pointer dereference
- Resource leak
- Infinite loop
- Thread safety issues
- Security vulnerabilities

**실행**:
```bash
# 검사
mvn spotbugs:check

# GUI로 결과 보기
mvn spotbugs:gui

# 리포트 위치
target/spotbugsXml.xml
target/site/spotbugs.html
```

### 3. PMD

**목적**: 코드 품질 및 안티패턴 탐지

**설정 파일**: `pmd-ruleset.xml`

**주요 규칙**:
- Best practices
- Code style
- Design patterns
- Error handling
- Performance

**실행**:
```bash
# 검사
mvn pmd:check

# 리포트 생성
mvn pmd:pmd

# 리포트 위치
target/pmd.xml
target/site/pmd.html
```

### 4. JaCoCo

**목적**: 테스트 커버리지 측정

**목표**: 최소 50% 라인 커버리지

**실행**:
```bash
# 테스트 실행 및 커버리지 리포트 생성
mvn clean test jacoco:report

# 리포트 위치
target/site/jacoco/index.html
```

**커버리지 확인**:
```bash
# 브라우저에서 열기
open target/site/jacoco/index.html
```

---

## 로컬에서 실행

### 전체 코드 품질 체크

```bash
# 모든 품질 검사 실행
mvn clean verify

# 또는 개별 실행
mvn checkstyle:check
mvn spotbugs:check
mvn pmd:check
mvn test jacoco:report
```

### 특정 모듈만 검사

```bash
cd oauth-client
mvn checkstyle:check
```

### IDE 통합

#### IntelliJ IDEA

1. **Checkstyle Plugin 설치**:
   - Settings → Plugins → Checkstyle-IDEA 설치
   - Settings → Tools → Checkstyle → Configuration File 추가: `checkstyle.xml`

2. **SpotBugs Plugin 설치**:
   - Settings → Plugins → SpotBugs 설치
   - Analyze → SpotBugs 실행

3. **SonarLint Plugin** (권장):
   - Settings → Plugins → SonarLint 설치
   - 실시간 코드 분석

#### Eclipse

1. **Checkstyle Plugin**:
   - Help → Eclipse Marketplace → Checkstyle 검색 및 설치
   - Project → Properties → Checkstyle → Local Check Configuration 추가

2. **SpotBugs Plugin**:
   - Help → Eclipse Marketplace → SpotBugs 검색 및 설치

#### VS Code

1. **Java Extension Pack** 설치
2. **SonarLint Extension** 설치

---

## CI/CD 통합

### GitHub Actions

코드 품질 검사는 GitHub Actions에서 자동으로 실행됩니다.

**워크플로우**: `.github/workflows/ci.yml`

**실행 시점**:
- Push to `main`, `develop`, `claude/**` branches
- Pull Request to `main`, `develop`

**단계**:
1. Build and Test (Java 17, 21)
2. Code Quality Checks:
   - Checkstyle
   - SpotBugs
   - PMD
   - JaCoCo (커버리지 리포트)

**리포트 업로드**:
- Artifacts → `code-quality-reports`
- Codecov (커버리지 시각화)

### Pull Request 체크

PR 생성 시 자동으로 코드 품질 검사가 실행됩니다:

```
✓ Build and Test / Java 21
✓ Code Quality Checks
  ✓ Checkstyle
  ⚠ SpotBugs (warning allowed)
  ⚠ PMD (warning allowed)
  ✓ JaCoCo Report
```

---

## Pre-commit Hook

커밋 전에 자동으로 코드 품질 검사를 실행합니다.

### 설치

```bash
# Hook 설치
./hooks/install-hooks.sh

# 또는 수동 설치
cp hooks/pre-commit .git/hooks/
chmod +x .git/hooks/pre-commit
```

### 실행되는 검사

1. ✅ Checkstyle (필수)
2. ⚠ SpotBugs (경고)
3. ⚠ PMD (경고)
4. ✅ Unit Tests (필수)

### Hook 우회 (비권장)

```bash
# 긴급 상황에서만 사용
git commit --no-verify
```

---

## 코드 품질 기준

### 통과 기준

| 도구 | 기준 | 실패 시 |
|------|------|---------|
| **Checkstyle** | 0 errors | ❌ 커밋 차단 |
| **SpotBugs** | 0 high priority bugs | ⚠ 경고만 |
| **PMD** | 0 critical violations | ⚠ 경고만 |
| **JaCoCo** | ≥ 50% line coverage | ⚠ 경고만 |
| **Unit Tests** | 100% pass | ❌ 커밋 차단 |

### 예외 처리

#### Checkstyle 억제

```java
// CHECKSTYLE:OFF
public class LegacyCode {
    // Checkstyle 규칙 무시
}
// CHECKSTYLE:ON
```

#### SpotBugs 억제

```java
@SuppressFBWarnings(
    value = "NP_NULL_ON_SOME_PATH",
    justification = "Null check handled by caller"
)
public void method() {
    // ...
}
```

#### PMD 억제

```java
@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
public void method() {
    for (int i = 0; i < 10; i++) {
        Object obj = new Object(); // 필요한 경우
    }
}
```

---

## 리포트 확인

### 로컬 리포트

```bash
# HTML 리포트 생성
mvn site

# 리포트 위치
target/site/index.html
```

### GitHub Actions 리포트

1. GitHub → Actions → 워크플로우 선택
2. Artifacts → `code-quality-reports` 다운로드
3. 압축 해제 후 HTML 파일 열기

### Codecov 리포트

https://codecov.io/gh/archmagece-backyard/sb-oauth-java

---

## 문제 해결

### Checkstyle 오류: Line too long

```bash
# 자동 포맷팅 (IntelliJ)
Ctrl+Alt+L (Windows/Linux)
Cmd+Option+L (macOS)
```

### SpotBugs: Resource not closed

```java
// ❌ Bad
FileInputStream fis = new FileInputStream("file.txt");

// ✅ Good
try (FileInputStream fis = new FileInputStream("file.txt")) {
    // ...
}
```

### PMD: Avoid using implementation types like 'ArrayList'

```java
// ❌ Bad
ArrayList<String> list = new ArrayList<>();

// ✅ Good
List<String> list = new ArrayList<>();
```

### JaCoCo: 낮은 커버리지

```bash
# 누락된 테스트 확인
mvn jacoco:report
open target/site/jacoco/index.html

# 빨간색 = 커버되지 않은 코드
# 초록색 = 커버된 코드
```

---

## 베스트 프랙티스

1. **커밋 전에 로컬에서 검사**: `mvn verify`
2. **IDE 플러그인 사용**: 실시간 피드백
3. **작은 커밋**: 문제 발견 및 수정 용이
4. **리뷰 전에 품질 체크**: PR 생성 전 검사
5. **억제는 최소화**: 정당한 이유가 있을 때만

---

## 추가 리소스

- [Checkstyle 규칙](https://checkstyle.sourceforge.io/checks.html)
- [SpotBugs 버그 패턴](https://spotbugs.readthedocs.io/en/latest/bugDescriptions.html)
- [PMD 규칙](https://pmd.github.io/latest/pmd_rules_java.html)
- [JaCoCo 문서](https://www.jacoco.org/jacoco/trunk/doc/)

---

**질문이나 제안이 있으신가요?**
[GitHub Issues](https://github.com/archmagece-backyard/sb-oauth-java/issues)에 남겨주세요!
