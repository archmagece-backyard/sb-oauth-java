# sb-oauth-java 예제 프로젝트

sb-oauth-java를 실제로 사용하는 방법을 보여주는 예제 프로젝트 모음입니다.

## 예제 목록

### 1. [spring-boot-basic](spring-boot-basic/)
**난이도**: ⭐ 초급
**소요 시간**: 15분

Spring Boot를 사용한 기본 Naver 로그인 예제입니다.

**포함 내용:**
- Naver OAuth 로그인
- 사용자 프로필 조회
- 세션 기반 토큰 관리
- Thymeleaf 템플릿

**배울 수 있는 것:**
- sb-oauth-java 기본 사용법
- Spring Boot 통합
- OAuth 플로우 전체 과정

---

### 2. [multi-provider](multi-provider/)
**난이도**: ⭐⭐ 중급
**소요 시간**: 30분

여러 OAuth 제공자(Naver, Kakao, Google)를 동시에 지원하는 예제입니다.

**포함 내용:**
- Naver, Kakao, Google 로그인
- Provider별 설정 관리
- 통합 사용자 관리
- Redis 토큰 저장소

**배울 수 있는 것:**
- 다중 Provider 지원
- Provider별 특이사항 처리
- 토큰 저장소 사용

---

### 3. [redis-storage](redis-storage/)
**난이도**: ⭐⭐⭐ 고급
**소요 시간**: 45분

Redis를 사용한 분산 토큰 저장소 예제입니다.

**포함 내용:**
- Redis 토큰 저장소
- 토큰 자동 갱신
- 세션 클러스터링
- Docker Compose 환경

**배울 수 있는 것:**
- 프로덕션 환경 설정
- 분산 시스템 구축
- 토큰 관리 최적화

---

## 빠른 시작

### 사전 요구사항

- Java 21+
- Maven 3.8+
- Docker & Docker Compose (redis-storage 예제)
- OAuth Provider 계정 (Naver, Kakao, Google 중 하나 이상)

### OAuth Provider 앱 등록

각 예제를 실행하기 전에 OAuth Provider에 앱을 등록해야 합니다:

#### Naver
1. https://developers.naver.com/apps/ 접속
2. **애플리케이션 등록** 클릭
3. **네이버 로그인** API 선택
4. Callback URL: `http://localhost:8080/oauth/callback/naver`
5. Client ID, Client Secret 복사

#### Kakao
1. https://developers.kakao.com/ 접속
2. **내 애플리케이션** → **애플리케이션 추가하기**
3. **플랫폼** → **Web** → `http://localhost:8080` 추가
4. **카카오 로그인** → **Redirect URI**: `http://localhost:8080/oauth/callback/kakao`
5. REST API 키 복사

#### Google
1. https://console.cloud.google.com/ 접속
2. 프로젝트 생성 → **APIs & Services** → **Credentials**
3. **OAuth 2.0 Client IDs** 생성
4. Authorized redirect URIs: `http://localhost:8080/oauth/callback/google`
5. Client ID, Client Secret 복사

### 예제 실행

각 예제 디렉토리로 이동하여 README를 따라하세요:

```bash
# 예제 1: Spring Boot 기본
cd examples/spring-boot-basic
cp .env.example .env
# .env 파일에 Client ID, Secret 입력
mvn spring-boot:run

# 예제 2: 다중 Provider
cd examples/multi-provider
cp .env.example .env
# .env 파일에 각 Provider의 Client ID, Secret 입력
mvn spring-boot:run

# 예제 3: Redis 저장소
cd examples/redis-storage
cp .env.example .env
# .env 파일에 Client ID, Secret 입력
docker-compose up -d
mvn spring-boot:run
```

---

## 예제 구조

모든 예제는 동일한 구조를 따릅니다:

```
example-name/
├── pom.xml                   # Maven 의존성
├── README.md                 # 상세 설명 및 실행 방법
├── .env.example              # 환경 변수 템플릿
├── docker-compose.yml        # Docker 설정 (필요한 경우)
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/oauth/
│   │   │       ├── Application.java       # 메인 클래스
│   │   │       ├── config/                # 설정 클래스
│   │   │       ├── controller/            # 컨트롤러
│   │   │       └── service/               # 비즈니스 로직
│   │   └── resources/
│   │       ├── application.yml            # Spring Boot 설정
│   │       └── templates/                 # Thymeleaf 템플릿
│   └── test/
│       └── java/                          # 테스트 코드
└── target/                   # 빌드 결과 (gitignore)
```

---

## 문제 해결

### Maven 빌드 실패

```bash
# 부모 프로젝트 먼저 빌드
cd ../..  # sb-oauth-java 루트로 이동
mvn clean install
cd examples/spring-boot-basic
mvn clean package
```

### OAuth 로그인 실패

**증상**: `redirect_uri_mismatch` 에러

**해결**:
1. OAuth Provider Console에서 Redirect URI 확인
2. `application.yml`의 redirect-uri와 정확히 일치하는지 확인
3. Protocol (`http` vs `https`), Port, Path 모두 일치해야 함

### Redis 연결 실패 (redis-storage 예제)

```bash
# Redis 컨테이너 상태 확인
docker ps | grep redis

# Redis 재시작
docker-compose down
docker-compose up -d redis

# Redis 접속 테스트
docker exec -it redis redis-cli ping
# 응답: PONG
```

---

## 추가 학습 자료

- [사용자 가이드](../docs/USER_GUIDE.md) - 전체 사용법
- [아키텍처 가이드](../docs/ARCHITECTURE.md) - 내부 구조
- [FAQ](../docs/FAQ.md) - 자주 묻는 질문

---

## 기여

새로운 예제를 추가하고 싶으신가요?

1. Fork this repository
2. Create example directory: `examples/your-example-name/`
3. Add README.md with clear instructions
4. Submit Pull Request

예제 아이디어:
- JWT 토큰 발급 예제
- React + Spring Boot 예제
- Microservices 예제
- GraphQL 통합 예제

---

**질문이나 문제가 있으신가요?**
[GitHub Issues](https://github.com/archmagece-backyard/sb-oauth-java/issues)에 남겨주세요!
