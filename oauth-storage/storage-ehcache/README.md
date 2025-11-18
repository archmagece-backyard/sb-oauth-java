# oauth-storage-ehcache

Ehcache-based token storage for single-server OAuth applications.

## Features

- ✅ In-memory token storage with disk overflow
- ✅ Automatic TTL (Time To Live) management
- ✅ Zero external dependencies (no Redis/database required)
- ✅ High performance (in-process cache)
- ✅ Optional disk persistence
- ✅ Thread-safe operations

## When to Use

Use Ehcache storage when:
- Running on a **single server** (not distributed)
- Need fast in-memory caching
- Want to avoid Redis/database setup
- Development or testing environments
- Small to medium traffic (< 10,000 concurrent users)
- Prefer simplicity over distributed features

## When NOT to Use

Avoid Ehcache storage when:
- Running **multiple servers** (horizontal scaling)
- Need to share sessions across servers
- Require distributed cache
- Building microservices architecture

**Use Redis storage instead for distributed environments.**

## Installation

### Maven Dependency

```xml
<dependency>
    <groupId>org.scriptonbasestar.oauth</groupId>
    <artifactId>oauth-storage-ehcache</artifactId>
    <version>sb-oauth-20181219-3-DEV</version>
</dependency>

<!-- Ehcache 2.x -->
<dependency>
    <groupId>net.sf.ehcache</groupId>
    <artifactId>ehcache</artifactId>
    <version>2.10.9.2</version>
</dependency>
```

⚠️ **Version Note**: This module uses **Ehcache 2.x** (legacy). For Ehcache 3.x support, see migration guide below.

## Usage

### Basic Configuration

```java
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import org.scripton.oauth.storage.ehcache.EhcacheTokenStorage;

// Create cache manager
CacheManager cacheManager = CacheManager.getInstance();

// Configure cache
CacheConfiguration cacheConfig = new CacheConfiguration()
    .name("oauthTokenCache")
    .maxEntriesLocalHeap(10000)              // Max 10,000 tokens in memory
    .timeToLiveSeconds(3600)                  // Token expires after 1 hour
    .timeToIdleSeconds(1800);                 // Evict if idle for 30 min

// Create cache
Cache cache = new Cache(cacheConfig);
cacheManager.addCache(cache);

// Create token storage
TokenStorage tokenStorage = new EhcacheTokenStorage(cache);
```

### Using XML Configuration

**ehcache.xml** (place in `src/main/resources`):
```xml
<?xml version="1.0" encoding="UTF-8"?>
<ehcache xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:noNamespaceSchemaLocation="http://ehcache.org/ehcache.xsd"
         updateCheck="false">

    <!-- Default cache configuration -->
    <defaultCache
        maxEntriesLocalHeap="1000"
        eternal="false"
        timeToIdleSeconds="300"
        timeToLiveSeconds="600"
        memoryStoreEvictionPolicy="LRU"/>

    <!-- OAuth token cache -->
    <cache name="oauthTokenCache"
           maxEntriesLocalHeap="10000"
           maxEntriesLocalDisk="100000"
           eternal="false"
           timeToLiveSeconds="3600"
           timeToIdleSeconds="1800"
           memoryStoreEvictionPolicy="LRU"
           diskPersistent="false"
           diskExpiryThreadIntervalSeconds="120"
           overflowToDisk="true">
    </cache>

    <!-- OAuth state cache (short-lived for CSRF protection) -->
    <cache name="oauthStateCache"
           maxEntriesLocalHeap="5000"
           eternal="false"
           timeToLiveSeconds="600"
           timeToIdleSeconds="600"
           memoryStoreEvictionPolicy="LFU"
           overflowToDisk="false">
    </cache>
</ehcache>
```

**Java code:**
```java
import net.sf.ehcache.CacheManager;

// Load from ehcache.xml
CacheManager cacheManager = CacheManager.create(
    getClass().getResourceAsStream("/ehcache.xml")
);

// Get cache by name
Cache tokenCache = cacheManager.getCache("oauthTokenCache");

// Create storage
TokenStorage tokenStorage = new EhcacheTokenStorage(tokenCache);
```

### Store and Retrieve Tokens

```java
import org.scriptonbasestar.oauth.client.model.Token;

// Store token
Token accessToken = new Token("access_token_value_here");
tokenStorage.store("user123:access_token", accessToken);

// Retrieve token
Token token = tokenStorage.load("user123:access_token");
System.out.println("Access Token: " + token.getValue());

// Remove token
tokenStorage.drop("user123:access_token");
```

### Complete Example

```java
public class EhcacheStorageExample {
    public static void main(String[] args) {
        // 1. Create cache manager
        CacheManager cacheManager = CacheManager.getInstance();

        // 2. Configure cache programmatically
        CacheConfiguration config = new CacheConfiguration()
            .name("oauthTokenCache")
            .maxEntriesLocalHeap(10000)
            .timeToLiveSeconds(3600)
            .timeToIdleSeconds(1800)
            .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LRU);

        Cache cache = new Cache(config);
        cacheManager.addCache(cache);

        // 3. Create token storage
        TokenStorage tokenStorage = new EhcacheTokenStorage(cache);

        // 4. Use with OAuth connector
        OAuth2NaverConfig naverConfig = OAuth2NaverConfig.builder()
            .clientId("YOUR_CLIENT_ID")
            .clientSecret("YOUR_CLIENT_SECRET")
            .redirectUri("http://localhost:8080/callback")
            .scope("profile,email")
            .build();

        TokenExtractor<OAuth2NaverTokenRes> extractor =
            new JsonTokenExtractor<>(new TypeReference<OAuth2NaverTokenRes>() {});

        OAuth2NaverAccesstokenFunction tokenFunction =
            new OAuth2NaverAccesstokenFunction(naverConfig, extractor, tokenStorage);

        // OAuth flow continues...
    }
}
```

## Spring Boot Integration

### Configuration Class

```java
@Configuration
public class EhcacheConfig {

    @Bean
    public CacheManager ehCacheManager() {
        CacheManager cacheManager = CacheManager.getInstance();

        // Token cache configuration
        CacheConfiguration tokenCacheConfig = new CacheConfiguration()
            .name("oauthTokenCache")
            .maxEntriesLocalHeap(10000)
            .timeToLiveSeconds(3600)      // 1 hour
            .timeToIdleSeconds(1800)       // 30 minutes idle
            .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LRU);

        Cache tokenCache = new Cache(tokenCacheConfig);
        cacheManager.addCache(tokenCache);

        // State cache configuration
        CacheConfiguration stateCacheConfig = new CacheConfiguration()
            .name("oauthStateCache")
            .maxEntriesLocalHeap(5000)
            .timeToLiveSeconds(600)        // 10 minutes
            .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU);

        Cache stateCache = new Cache(stateCacheConfig);
        cacheManager.addCache(stateCache);

        return cacheManager;
    }

    @Bean
    public TokenStorage tokenStorage(CacheManager cacheManager) {
        Cache cache = cacheManager.getCache("oauthTokenCache");
        return new EhcacheTokenStorage(cache);
    }

    @Bean
    public StateStorage stateStorage(CacheManager cacheManager) {
        Cache cache = cacheManager.getCache("oauthStateCache");
        return new EhcacheStateStorage(cache);
    }

    @PreDestroy
    public void cleanup() {
        CacheManager.getInstance().shutdown();
    }
}
```

### Using XML Configuration in Spring Boot

**application.yml:**
```yaml
spring:
  cache:
    type: ehcache
    ehcache:
      config: classpath:ehcache.xml
```

**Configuration:**
```java
@Configuration
@EnableCaching
public class EhcacheConfig {

    @Bean
    public CacheManager cacheManager() {
        return CacheManager.create(
            getClass().getResourceAsStream("/ehcache.xml")
        );
    }

    @Bean
    public TokenStorage tokenStorage(CacheManager cacheManager) {
        return new EhcacheTokenStorage(cacheManager.getCache("oauthTokenCache"));
    }
}
```

## Cache Configuration Options

### Key Parameters

| Parameter | Description | Recommended Value |
|-----------|-------------|-------------------|
| `maxEntriesLocalHeap` | Max items in memory | 10,000 (adjust based on RAM) |
| `maxEntriesLocalDisk` | Max items on disk | 100,000 |
| `timeToLiveSeconds` | Max lifetime (TTL) | 3600 (1 hour for tokens) |
| `timeToIdleSeconds` | Evict if not accessed | 1800 (30 min) |
| `memoryStoreEvictionPolicy` | Eviction strategy | LRU (Least Recently Used) |
| `overflowToDisk` | Spill to disk when full | true (if persistence needed) |
| `diskPersistent` | Persist on shutdown | false (tokens are temporary) |
| `eternal` | Never expire | false |

### Eviction Policies

| Policy | Description | Use Case |
|--------|-------------|----------|
| **LRU** | Least Recently Used | General purpose, access time matters |
| **LFU** | Least Frequently Used | Hot data, frequency matters |
| **FIFO** | First In First Out | Time-based, order matters |

**Example:**
```java
CacheConfiguration config = new CacheConfiguration()
    .name("oauthTokenCache")
    .maxEntriesLocalHeap(10000)
    .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LRU);
```

## Disk Persistence

### Enable Disk Overflow

```xml
<cache name="oauthTokenCache"
       maxEntriesLocalHeap="1000"
       maxEntriesLocalDisk="10000"
       overflowToDisk="true"
       diskPersistent="false">
</cache>
```

**Java:**
```java
CacheConfiguration config = new CacheConfiguration()
    .name("oauthTokenCache")
    .maxEntriesLocalHeap(1000)
    .maxEntriesLocalDisk(10000)
    .overflowToDisk(true)
    .diskPersistent(false);
```

⚠️ **Security Warning**: If storing sensitive tokens on disk, ensure:
- Disk encryption is enabled
- Proper file permissions (chmod 600)
- Secure deletion on shutdown
- Consider encryption at application level

## Performance Tuning

### Memory Sizing

```java
// Calculate heap size based on token size
// Average token: ~500 bytes (access_token + metadata)
// 10,000 tokens ≈ 5 MB
// Add 50% overhead: ~7.5 MB

CacheConfiguration config = new CacheConfiguration()
    .name("oauthTokenCache")
    .maxEntriesLocalHeap(10000)    // ~7.5 MB
    .maxBytesLocalHeap(8, MemoryUnit.MEGABYTES);
```

### Statistics and Monitoring

```java
// Enable statistics
CacheConfiguration config = new CacheConfiguration()
    .name("oauthTokenCache")
    .statistics(true);

Cache cache = new Cache(config);
cacheManager.addCache(cache);

// Get statistics
System.out.println("Cache hits: " + cache.getStatistics().getCacheHits());
System.out.println("Cache misses: " + cache.getStatistics().getCacheMisses());
System.out.println("Hit ratio: " + cache.getStatistics().getCacheHitRatio());
System.out.println("Size: " + cache.getSize());
```

### Health Check

```java
@Component
public class EhcacheHealthCheck {

    @Autowired
    private CacheManager cacheManager;

    public boolean isHealthy() {
        try {
            Cache cache = cacheManager.getCache("oauthTokenCache");
            return cache != null && cache.getStatus() == Status.STATUS_ALIVE;
        } catch (Exception e) {
            return false;
        }
    }

    public Map<String, Object> getStats() {
        Cache cache = cacheManager.getCache("oauthTokenCache");
        Map<String, Object> stats = new HashMap<>();
        stats.put("size", cache.getSize());
        stats.put("hits", cache.getStatistics().getCacheHits());
        stats.put("misses", cache.getStatistics().getCacheMisses());
        stats.put("hitRatio", cache.getStatistics().getCacheHitRatio());
        return stats;
    }
}
```

## Comparison: Ehcache vs Redis vs Local

| Feature | Ehcache | Redis | Local (In-Memory) |
|---------|---------|-------|-------------------|
| **Distributed** | ❌ No | ✅ Yes | ❌ No |
| **Persistence** | ⚠️ Optional (disk) | ✅ Yes (RDB/AOF) | ❌ No |
| **TTL Support** | ✅ Yes | ✅ Yes | ⚠️ Manual |
| **Performance** | ⚡⚡ Very Fast | ⚡ Fast (network) | ⚡⚡⚡ Fastest |
| **Memory** | Heap + Disk | External process | Heap only |
| **Setup** | Simple | Requires Redis server | Trivial |
| **Scalability** | Single server | Multi-server | Single server |
| **Use Case** | Single server production | Distributed systems | Dev/Test only |

## Migration to Ehcache 3.x

Ehcache 2.x is legacy. For new projects, consider Ehcache 3.x (JSR-107 compliant):

### Ehcache 3.x Dependency

```xml
<dependency>
    <groupId>org.ehcache</groupId>
    <artifactId>ehcache</artifactId>
    <version>3.10.8</version>
</dependency>
```

### Migration Example

**Ehcache 2.x:**
```java
CacheManager cacheManager = CacheManager.getInstance();
Cache cache = cacheManager.getCache("oauthTokenCache");
cache.put(new Element("key", token));
```

**Ehcache 3.x:**
```java
CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build(true);
Cache<String, Token> cache = cacheManager.createCache("oauthTokenCache",
    CacheConfigurationBuilder.newCacheConfigurationBuilder(
        String.class, Token.class,
        ResourcePoolsBuilder.heap(10000)
    )
);
cache.put("key", token);
```

## Troubleshooting

### OutOfMemoryError: Java heap space

**Cause**: Too many tokens in memory

**Solution**:
1. Reduce `maxEntriesLocalHeap`
2. Enable `overflowToDisk`
3. Increase JVM heap size: `-Xmx2g`
4. Lower `timeToLiveSeconds` to expire tokens faster

### ConcurrentModificationException

**Cause**: Accessing cache during iteration

**Solution**: Ehcache is thread-safe, but use proper iteration:
```java
// Safe iteration
List<String> keys = cache.getKeys();
for (String key : keys) {
    Element element = cache.get(key);
    // Process element
}
```

### Disk Store Path Permission Denied

**Cause**: Ehcache cannot write to disk store directory

**Solution**:
```xml
<ehcache>
    <diskStore path="/tmp/ehcache"/>
    <!-- Or use java.io.tmpdir -->
    <diskStore path="java.io.tmpdir/ehcache"/>
</ehcache>
```

Or programmatically:
```java
System.setProperty("net.sf.ehcache.diskStore.path", "/tmp/ehcache");
```

### Cache Not Found

**Cause**: Cache name mismatch

**Solution**:
```java
// Ensure cache is added to manager
cacheManager.addCache(cache);

// Check cache exists
if (cacheManager.cacheExists("oauthTokenCache")) {
    Cache cache = cacheManager.getCache("oauthTokenCache");
}
```

## Best Practices

1. **Separate Caches**: Use different caches for tokens and states
2. **TTL Matching**: Set cache TTL to match token expiration from OAuth provider
3. **Statistics**: Enable statistics in development for performance tuning
4. **Shutdown**: Always call `cacheManager.shutdown()` on application shutdown
5. **Security**: Never persist sensitive tokens to disk without encryption
6. **Monitoring**: Monitor cache hit ratio and adjust size accordingly
7. **Testing**: Test eviction policies under load

## Additional Resources

- [Ehcache 2.x Documentation](http://www.ehcache.org/documentation/2.10/)
- [Ehcache 3.x Documentation](https://www.ehcache.org/documentation/3.10/)
- [Spring Cache Abstraction](https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html#cache)
- [Migration Guide: 2.x to 3.x](https://www.ehcache.org/documentation/3.0/migration-guide.html)

## License

Apache License 2.0
