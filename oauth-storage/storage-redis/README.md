# oauth-storage-redis

Redis-based token and state storage for distributed OAuth applications.

## Features

- ✅ Distributed token storage across multiple servers
- ✅ Automatic TTL (Time To Live) management
- ✅ Session sharing in clustered environments
- ✅ High performance with in-memory storage
- ✅ Persistence options (RDB, AOF)

## When to Use

Use Redis storage when:
- Running multiple application servers (horizontal scaling)
- Need to share OAuth sessions across servers
- Require high-performance token storage
- Want automatic token expiration
- Need persistence and backup

## Installation

### Maven Dependency

```xml
<dependency>
    <groupId>org.scriptonbasestar.oauth</groupId>
    <artifactId>oauth-storage-redis</artifactId>
    <version>sb-oauth-20181219-3-DEV</version>
</dependency>

<!-- Redis client -->
<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
    <version>5.2.0</version>
</dependency>
```

### Redis Server

Install Redis server:

```bash
# Ubuntu/Debian
sudo apt-get install redis-server

# macOS
brew install redis

# Docker
docker run -d -p 6379:6379 redis:7-alpine
```

## Usage

### Basic Configuration

```java
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import org.scripton.oauth.storage.redis.RedisTokenStorage;
import org.scripton.oauth.storage.redis.RedisStateStorage;

// Create Jedis pool
JedisPoolConfig poolConfig = new JedisPoolConfig();
poolConfig.setMaxTotal(20);
poolConfig.setMaxIdle(10);
poolConfig.setMinIdle(5);

JedisPool jedisPool = new JedisPool(poolConfig, "localhost", 6379);

// Create token storage
TokenStorage tokenStorage = new RedisTokenStorage(jedisPool);

// Create state storage
StateStorage stateStorage = new RedisStateStorage(jedisPool);
```

### With Password Authentication

```java
JedisPool jedisPool = new JedisPool(
    poolConfig,
    "localhost",    // host
    6379,           // port
    2000,           // timeout
    "yourpassword"  // password
);
```

### Redis Cluster

```java
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.HostAndPort;

Set<HostAndPort> nodes = new HashSet<>();
nodes.add(new HostAndPort("node1", 6379));
nodes.add(new HostAndPort("node2", 6379));
nodes.add(new HostAndPort("node3", 6379));

JedisCluster jedisCluster = new JedisCluster(nodes, poolConfig);

// Use with cluster-aware storage implementation
```

### Token Storage with TTL

```java
// Store token with automatic expiration
Token accessToken = new Token("access_token_value");
tokenStorage.save("user123:access_token", accessToken);

// Token will expire based on OAuth provider's expires_in value
// For Naver: 1 hour, Kakao: 6 hours

// Retrieve token
Token token = tokenStorage.get("user123:access_token");

// Remove token
tokenStorage.remove("user123:access_token");
```

### State Storage with TTL

```java
// Store state (expires after 10 minutes for security)
State state = new State("random_state_value");
stateStorage.save(state.getValue(), state);

// Validate state
if (stateStorage.isValid("random_state_value")) {
    State retrievedState = stateStorage.get("random_state_value");
    // Process OAuth callback
}

// Remove after use
stateStorage.remove("random_state_value");
```

## Spring Boot Integration

```java
@Configuration
public class RedisConfig {

    @Bean
    public JedisPool jedisPool(
        @Value("${redis.host:localhost}") String host,
        @Value("${redis.port:6379}") int port,
        @Value("${redis.password:}") String password
    ) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(20);
        poolConfig.setMaxIdle(10);
        poolConfig.setMinIdle(5);
        poolConfig.setTestOnBorrow(true);

        if (password != null && !password.isEmpty()) {
            return new JedisPool(poolConfig, host, port, 2000, password);
        }
        return new JedisPool(poolConfig, host, port);
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

**application.yml**:
```yaml
redis:
  host: localhost
  port: 6379
  password: ${REDIS_PASSWORD:}
```

## Key Naming Convention

Keys are stored with prefixes for organization:

```
oauth:token:{userId}:access_token
oauth:token:{userId}:refresh_token
oauth:state:{stateValue}
```

Example:
```
oauth:token:user123:access_token
oauth:token:user123:refresh_token
oauth:state:abc123xyz
```

## Performance Considerations

### Connection Pooling

```java
JedisPoolConfig poolConfig = new JedisPoolConfig();
poolConfig.setMaxTotal(20);       // Max connections
poolConfig.setMaxIdle(10);        // Max idle connections
poolConfig.setMinIdle(5);         // Min idle connections
poolConfig.setTestOnBorrow(true); // Validate before use
poolConfig.setTestOnReturn(true); // Validate after use
poolConfig.setMaxWaitMillis(2000); // Max wait time
```

### Pipelining for Bulk Operations

```java
// Not yet implemented in RedisTokenStorage
// Future enhancement for batch operations
```

## Monitoring

### Check Redis Status

```bash
# Connect to Redis CLI
redis-cli

# Check memory usage
INFO memory

# List all keys (dev only, slow in production)
KEYS oauth:*

# Get TTL of a key
TTL oauth:token:user123:access_token

# Monitor real-time commands
MONITOR
```

### Health Check

```java
@Component
public class RedisHealthCheck {

    @Autowired
    private JedisPool jedisPool;

    public boolean isHealthy() {
        try (Jedis jedis = jedisPool.getResource()) {
            return "PONG".equals(jedis.ping());
        } catch (Exception e) {
            return false;
        }
    }
}
```

## Production Deployment

### Redis Configuration

**redis.conf**:
```conf
# Bind to specific IP
bind 0.0.0.0

# Set password
requirepass your_strong_password

# Max memory policy
maxmemory 2gb
maxmemory-policy allkeys-lru

# Enable persistence
save 900 1
save 300 10
save 60 10000

# AOF
appendonly yes
appendfsync everysec
```

### Docker Deployment

```bash
docker run -d \
  --name oauth-redis \
  -p 6379:6379 \
  -v /path/to/redis.conf:/usr/local/etc/redis/redis.conf \
  -v /path/to/data:/data \
  redis:7-alpine \
  redis-server /usr/local/etc/redis/redis.conf
```

### High Availability

Use Redis Sentinel or Redis Cluster for production:

```java
// Redis Sentinel
Set<String> sentinels = new HashSet<>();
sentinels.add("sentinel1:26379");
sentinels.add("sentinel2:26379");
sentinels.add("sentinel3:26379");

JedisSentinelPool sentinelPool = new JedisSentinelPool(
    "mymaster",
    sentinels,
    poolConfig,
    2000,
    "password"
);
```

## Troubleshooting

### Connection Refused

**Cause**: Redis server not running or wrong host/port

**Solution**:
```bash
# Check if Redis is running
redis-cli ping
# Should return PONG

# Start Redis
redis-server

# Check port
netstat -an | grep 6379
```

### Authentication Failed

**Cause**: Password mismatch

**Solution**: Verify password in redis.conf matches application config

### Out of Memory

**Cause**: Redis maxmemory limit reached

**Solution**: Increase maxmemory or enable eviction policy in redis.conf

### Slow Performance

**Cause**: Too many connections or inefficient queries

**Solution**:
- Increase max connections in pool config
- Use pipelining for bulk operations
- Monitor with `redis-cli --latency`

## Comparison: Redis vs Ehcache vs Local

| Feature | Redis | Ehcache | Local |
|---------|-------|---------|-------|
| **Distributed** | ✅ Yes | ❌ No | ❌ No |
| **Persistence** | ✅ Yes | ⚠️ Optional | ❌ No |
| **TTL Support** | ✅ Yes | ✅ Yes | ⚠️ Manual |
| **Performance** | ⚡ Fast | ⚡⚡ Very Fast | ⚡⚡⚡ Fastest |
| **Memory** | External | Heap | Heap |
| **Use Case** | Multi-server | Single server | Dev/Test |

## License

Apache License 2.0
