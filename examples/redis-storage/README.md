# Redis Storage Example

**난이도**: ⭐⭐⭐ 고급
**소요 시간**: 45분
**상태**: 📝 계획 중 (Coming Soon)

Redis를 사용한 분산 토큰 저장소와 프로덕션 수준의 OAuth 구현 예제입니다.

## 예정된 기능

### 주요 기능

1. **Redis 토큰 저장소**
   - 분산 환경 지원
   - 자동 TTL 관리
   - Connection Pooling
   - Redis Cluster 지원

2. **토큰 자동 갱신**
   - 만료 전 자동 갱신
   - Background job (Scheduled task)
   - 갱신 실패 시 재시도

3. **세션 클러스터링**
   - Spring Session with Redis
   - 다중 서버 환경 지원
   - Session 공유

4. **프로덕션 최적화**
   - Connection pooling
   - Circuit breaker
   - Rate limiting
   - Monitoring & Metrics

### 프로젝트 구조 (계획)

```
redis-storage/
├── pom.xml
├── README.md
├── .env.example
├── docker-compose.yml                          # Redis + Redis Commander
└── src/
    └── main/
        ├── java/com/example/oauth/
        │   ├── Application.java
        │   ├── config/
        │   │   ├── OAuthConfig.java
        │   │   ├── RedisConfig.java            # Redis 설정
        │   │   └── SessionConfig.java          # Session 설정
        │   ├── controller/
        │   │   └── OAuthController.java
        │   ├── service/
        │   │   ├── TokenRefreshService.java    # 자동 갱신
        │   │   └── TokenCacheService.java      # 캐싱
        │   ├── scheduler/
        │   │   └── TokenRefreshScheduler.java  # Background job
        │   └── storage/
        │       └── RedisTokenStorageImpl.java  # Custom implementation
        └── resources/
            ├── application.yml
            └── application-prod.yml             # 프로덕션 설정
```

### Docker Compose

```yaml
version: '3.8'
services:
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data
    command: redis-server --appendonly yes --maxmemory 256mb --maxmemory-policy allkeys-lru
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 3s
      retries: 3

  redis-commander:
    image: rediscommander/redis-commander:latest
    ports:
      - "8081:8081"
    environment:
      - REDIS_HOSTS=local:redis:6379
    depends_on:
      - redis

  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - REDIS_HOST=redis
      - REDIS_PORT=6379
    depends_on:
      redis:
        condition: service_healthy

volumes:
  redis-data:
```

### Redis Configuration

```java
@Configuration
public class RedisConfig {

    @Value("${redis.host}")
    private String host;

    @Value("${redis.port}")
    private int port;

    @Bean
    public JedisPool jedisPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();

        // Connection pool settings
        poolConfig.setMaxTotal(50);              // 최대 50개 연결
        poolConfig.setMaxIdle(10);               // 유휴 연결 10개
        poolConfig.setMinIdle(5);                // 최소 5개 유지
        poolConfig.setTestOnBorrow(true);        // 연결 테스트
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);

        // Timeout settings
        poolConfig.setMaxWaitMillis(3000);       // 3초 대기
        poolConfig.setMinEvictableIdleTimeMillis(60000);  // 1분 유휴 시 제거

        return new JedisPool(poolConfig, host, port, 2000);  // 2초 timeout
    }

    @Bean
    public TokenStorage tokenStorage(JedisPool jedisPool) {
        return new RedisTokenStorage(jedisPool);
    }

    @Bean
    public StateStorage stateStorage(JedisPool jedisPool) {
        return new RedisStateStorage(jedisPool);
    }
}
```

### Token Refresh Service

```java
@Service
@Slf4j
public class TokenRefreshService {

    @Autowired
    private OAuth2NaverAccesstokenFunction tokenFunction;

    @Autowired
    private TokenStorage tokenStorage;

    /**
     * Get valid access token (auto-refresh if expired)
     */
    public String getValidAccessToken(String userId) {
        // Load token from Redis
        Token accessToken = tokenStorage.load(userId + ":access_token");
        Token refreshToken = tokenStorage.load(userId + ":refresh_token");

        if (accessToken == null || refreshToken == null) {
            throw new IllegalStateException("No tokens found for user: " + userId);
        }

        // Check if token is expired (with 5 minute buffer)
        if (isTokenExpired(userId)) {
            log.info("Access token expired, refreshing for user: {}", userId);

            try {
                // Refresh token
                OAuth2NaverTokenRes newToken = tokenFunction.refresh(refreshToken);

                // Store new tokens
                tokenStorage.store(userId + ":access_token", new Token(newToken.getAccess_token()));

                // Note: Naver refresh tokens don't change
                // But Kakao/Google might return new refresh token

                log.info("Token refreshed successfully for user: {}", userId);

                return newToken.getAccess_token();

            } catch (Exception e) {
                log.error("Token refresh failed for user: {}", userId, e);
                throw new RuntimeException("Token refresh failed", e);
            }
        }

        return accessToken.getValue();
    }

    private boolean isTokenExpired(String userId) {
        // Implementation: Check token expiration
        // Could use Redis TTL or separate expiry field
        return false;  // Placeholder
    }
}
```

### Scheduled Token Refresh

```java
@Component
@EnableScheduling
@Slf4j
public class TokenRefreshScheduler {

    @Autowired
    private TokenRefreshService tokenRefreshService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Refresh tokens every hour
     */
    @Scheduled(cron = "0 0 * * * *")  // Every hour
    public void refreshExpiredTokens() {
        log.info("Starting scheduled token refresh");

        List<String> userIds = userRepository.findAllActiveUserIds();

        for (String userId : userIds) {
            try {
                tokenRefreshService.getValidAccessToken(userId);
            } catch (Exception e) {
                log.error("Failed to refresh token for user: {}", userId, e);
                // Could notify user or mark for manual intervention
            }
        }

        log.info("Scheduled token refresh completed");
    }
}
```

### Redis Keys Structure

```
# Access Tokens
oauth:token:{userId}:access_token
TTL: 3600 seconds (1 hour)

# Refresh Tokens
oauth:token:{userId}:refresh_token
TTL: None (Naver) or 60 days (Kakao)

# State (CSRF)
oauth:state:{stateValue}
TTL: 600 seconds (10 minutes)

# Session
spring:session:sessions:{sessionId}
TTL: 1800 seconds (30 minutes)

# Token Metadata
oauth:token:{userId}:metadata
Fields:
  - issued_at: Unix timestamp
  - expires_in: Seconds
  - provider: naver/kakao/google
  - scope: Scopes granted
```

### Production Configuration

**application-prod.yml:**
```yaml
spring:
  session:
    store-type: redis
    redis:
      flush-mode: on_save
      namespace: spring:session
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    timeout: 2000ms
    jedis:
      pool:
        max-active: 50
        max-idle: 10
        min-idle: 5
        max-wait: 3000ms

oauth:
  token-refresh:
    enabled: true
    schedule: "0 0 * * * *"  # Every hour
    retry:
      max-attempts: 3
      backoff: 2000ms

logging:
  level:
    root: INFO
    com.example.oauth: DEBUG
    redis.clients.jedis: INFO
```

## 모니터링

### Redis Commander

Redis 데이터를 시각화하고 모니터링:

```bash
# Docker Compose로 Redis Commander 실행
docker-compose up -d redis-commander

# 접속
http://localhost:8081
```

### Health Check

```java
@RestController
@RequestMapping("/actuator")
public class HealthController {

    @Autowired
    private JedisPool jedisPool;

    @GetMapping("/health/redis")
    public Map<String, Object> redisHealth() {
        Map<String, Object> health = new HashMap<>();

        try (Jedis jedis = jedisPool.getResource()) {
            String pong = jedis.ping();
            health.put("status", "UP");
            health.put("ping", pong);
            health.put("active", jedisPool.getNumActive());
            health.put("idle", jedisPool.getNumIdle());
        } catch (Exception e) {
            health.put("status", "DOWN");
            health.put("error", e.getMessage());
        }

        return health;
    }
}
```

## 성능 최적화

### 1. Connection Pooling

- 최대 50개 연결
- 유휴 연결 5-10개 유지
- 연결 테스트 활성화

### 2. Token Caching

- 로컬 메모리에 5분간 캐싱
- Redis 조회 횟수 감소
- Caffeine Cache 사용

### 3. Batch Operations

```java
// ❌ 여러 번 조회
for (String userId : userIds) {
    Token token = tokenStorage.load(userId + ":access_token");
}

// ✅ Pipeline 사용
try (Jedis jedis = jedisPool.getResource()) {
    Pipeline pipeline = jedis.pipelined();
    for (String userId : userIds) {
        pipeline.get("oauth:token:" + userId + ":access_token");
    }
    List<Object> results = pipeline.syncAndReturnAll();
}
```

## 학습 목표

1. **Redis 운영**
   - Connection pooling 설정
   - TTL 관리
   - Key naming convention

2. **분산 시스템**
   - 세션 클러스터링
   - 토큰 공유
   - 상태 동기화

3. **프로덕션 배포**
   - Docker Compose
   - Health check
   - Monitoring

4. **성능 최적화**
   - Caching strategy
   - Connection management
   - Batch operations

## 다음 단계

이 예제를 마스터했다면:

1. Kubernetes로 배포
2. Redis Cluster 구성
3. Prometheus + Grafana 모니터링
4. Circuit Breaker 추가 (Resilience4j)

---

**📝 Note**: 이 예제는 현재 계획 단계입니다. 구현에 참여하고 싶으시다면 [GitHub Issues](https://github.com/archmagece-backyard/sb-oauth-java/issues)에 의견을 남겨주세요!

## 대안: 직접 구현하기

[Redis Storage README](../../oauth-storage/storage-redis/README.md)를 참고하여 직접 구현할 수 있습니다.

---

**Status**: Coming Soon
**Last Updated**: 2025-01-18
