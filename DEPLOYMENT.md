# Deployment Guide

Complete deployment guide for sb-oauth-java OAuth 2.0 library and applications.

## Table of Contents

1. [Deployment Options](#deployment-options)
2. [Docker Deployment](#docker-deployment)
3. [Kubernetes Deployment](#kubernetes-deployment)
4. [CI/CD Pipeline](#cicd-pipeline)
5. [Zero-Downtime Deployment](#zero-downtime-deployment)
6. [Rollback Strategy](#rollback-strategy)

---

## Deployment Options

### 1. Standalone JAR

**Build:**
```bash
mvn clean package -DskipTests
```

**Run:**
```bash
java -jar target/oauth-app.jar \
  --spring.profiles.active=production \
  --server.port=8080
```

### 2. Docker Container

**Build and run with Docker:**
```bash
docker build -t oauth-app:1.0.0 .
docker run -p 8080:8080 oauth-app:1.0.0
```

### 3. Kubernetes

**Deploy to Kubernetes cluster:**
```bash
kubectl apply -f k8s/
```

### 4. Cloud Platforms

**AWS Elastic Beanstalk:**
```bash
eb init -p java-21 oauth-app
eb create oauth-app-prod
eb deploy
```

**Google Cloud Run:**
```bash
gcloud run deploy oauth-app \
  --image gcr.io/project-id/oauth-app:1.0.0 \
  --platform managed
```

**Azure App Service:**
```bash
az webapp create --resource-group oauth-rg \
  --plan oauth-plan \
  --name oauth-app \
  --runtime "JAVA:21-java21"
```

---

## Docker Deployment

### Dockerfile

**Multi-stage Dockerfile:**

```dockerfile
# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /build

# Copy pom files for dependency caching
COPY pom.xml .
COPY oauth-client/pom.xml oauth-client/
COPY oauth-connector-*/pom.xml oauth-connector-*/
COPY oauth-storage-*/pom.xml oauth-storage-*/

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY oauth-client/src oauth-client/src
COPY oauth-connector-*/src oauth-connector-*/src
COPY oauth-storage-*/src oauth-storage-*/src

# Build application
RUN mvn clean package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

# Install curl for health checks
RUN apk add --no-cache curl

# Create non-root user
RUN addgroup -g 1001 -S oauth && \
    adduser -u 1001 -S oauth -G oauth

# Set working directory
WORKDIR /app

# Copy JAR from builder
COPY --from=builder /build/target/*.jar app.jar

# Change ownership
RUN chown -R oauth:oauth /app

# Switch to non-root user
USER oauth

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# JVM options
ENV JAVA_OPTS="-Xms512m -Xmx1g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# Run application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

### Docker Compose

**docker-compose.yml:**

```yaml
version: '3.8'

services:
  oauth-app:
    build:
      context: .
      dockerfile: Dockerfile
    image: oauth-app:1.0.0
    container_name: oauth-app
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: production

      # OAuth Provider Credentials
      NAVER_CLIENT_ID: ${NAVER_CLIENT_ID}
      NAVER_CLIENT_SECRET: ${NAVER_CLIENT_SECRET}
      KAKAO_CLIENT_ID: ${KAKAO_CLIENT_ID}
      KAKAO_CLIENT_SECRET: ${KAKAO_CLIENT_SECRET}
      GOOGLE_CLIENT_ID: ${GOOGLE_CLIENT_ID}
      GOOGLE_CLIENT_SECRET: ${GOOGLE_CLIENT_SECRET}
      FACEBOOK_CLIENT_ID: ${FACEBOOK_CLIENT_ID}
      FACEBOOK_CLIENT_SECRET: ${FACEBOOK_CLIENT_SECRET}

      # Redis Configuration
      OAUTH_STORAGE_TYPE: redis
      REDIS_HOST: redis
      REDIS_PORT: 6379
      REDIS_PASSWORD: ${REDIS_PASSWORD}

      # Security
      OAUTH_REDIRECT_BASE_URL: https://yourdomain.com

      # JVM Options
      JAVA_OPTS: >-
        -Xms1g
        -Xmx2g
        -XX:+UseG1GC
        -XX:MaxGCPauseMillis=200
        -XX:+HeapDumpOnOutOfMemoryError
        -XX:HeapDumpPath=/app/logs
    volumes:
      - ./logs:/app/logs
    depends_on:
      redis:
        condition: service_healthy
    restart: unless-stopped
    networks:
      - oauth-network
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 5s
      retries: 3
      start_period: 30s

  redis:
    image: redis:7.2-alpine
    container_name: oauth-redis
    command: redis-server --requirepass ${REDIS_PASSWORD} --maxmemory 256mb --maxmemory-policy allkeys-lru
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data
    restart: unless-stopped
    networks:
      - oauth-network
    healthcheck:
      test: ["CMD", "redis-cli", "--raw", "incr", "ping"]
      interval: 10s
      timeout: 3s
      retries: 3

  nginx:
    image: nginx:1.25-alpine
    container_name: oauth-nginx
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
      - ./nginx/ssl:/etc/nginx/ssl:ro
    depends_on:
      - oauth-app
    restart: unless-stopped
    networks:
      - oauth-network

volumes:
  redis-data:

networks:
  oauth-network:
    driver: bridge
```

**.env file:**

```bash
# OAuth Provider Credentials
NAVER_CLIENT_ID=your_naver_client_id
NAVER_CLIENT_SECRET=your_naver_client_secret
KAKAO_CLIENT_ID=your_kakao_client_id
KAKAO_CLIENT_SECRET=your_kakao_client_secret
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
FACEBOOK_CLIENT_ID=your_facebook_client_id
FACEBOOK_CLIENT_SECRET=your_facebook_client_secret

# Redis
REDIS_PASSWORD=your_strong_redis_password
```

### Deploy with Docker Compose

```bash
# Create .env file
cp .env.example .env
vim .env  # Edit with your credentials

# Build and start services
docker-compose up -d

# View logs
docker-compose logs -f oauth-app

# Check health
curl http://localhost:8080/actuator/health

# Stop services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

---

## Kubernetes Deployment

### Namespace

**namespace.yaml:**

```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: oauth-app
  labels:
    name: oauth-app
    environment: production
```

### ConfigMap

**configmap.yaml:**

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: oauth-app-config
  namespace: oauth-app
data:
  application.yml: |
    server:
      port: 8080
      shutdown: graceful

    spring:
      lifecycle:
        timeout-per-shutdown-phase: 30s

    oauth:
      security:
        state-expiration-seconds: 300
        require-https: true
        allow-localhost: false

      storage:
        type: redis
        redis:
          host: redis-service
          port: 6379
          database: 0
          timeout: 2000

    management:
      endpoints:
        web:
          exposure:
            include: health,info,metrics,prometheus
      endpoint:
        health:
          probes:
            enabled: true

    logging:
      level:
        root: INFO
        org.scriptonbasestar.oauth: INFO
```

### Secret

**Create secret from literals:**

```bash
kubectl create secret generic oauth-app-secrets \
  --from-literal=naver-client-id="your_naver_client_id" \
  --from-literal=naver-client-secret="your_naver_client_secret" \
  --from-literal=kakao-client-id="your_kakao_client_id" \
  --from-literal=kakao-client-secret="your_kakao_client_secret" \
  --from-literal=google-client-id="your_google_client_id" \
  --from-literal=google-client-secret="your_google_client_secret" \
  --from-literal=facebook-client-id="your_facebook_client_id" \
  --from-literal=facebook-client-secret="your_facebook_client_secret" \
  --from-literal=redis-password="your_redis_password" \
  --namespace=oauth-app
```

**Or from YAML (secret.yaml):**

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: oauth-app-secrets
  namespace: oauth-app
type: Opaque
stringData:
  naver-client-id: "your_naver_client_id"
  naver-client-secret: "your_naver_client_secret"
  kakao-client-id: "your_kakao_client_id"
  kakao-client-secret: "your_kakao_client_secret"
  google-client-id: "your_google_client_id"
  google-client-secret: "your_google_client_secret"
  facebook-client-id: "your_facebook_client_id"
  facebook-client-secret: "your_facebook_client_secret"
  redis-password: "your_redis_password"
```

### Deployment

**deployment.yaml:**

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: oauth-app
  namespace: oauth-app
  labels:
    app: oauth-app
    version: v1.0.0
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
  selector:
    matchLabels:
      app: oauth-app
  template:
    metadata:
      labels:
        app: oauth-app
        version: v1.0.0
      annotations:
        prometheus.io/scrape: "true"
        prometheus.io/port: "8080"
        prometheus.io/path: "/actuator/prometheus"
    spec:
      serviceAccountName: oauth-app

      # Pod Anti-Affinity (spread across nodes)
      affinity:
        podAntiAffinity:
          preferredDuringSchedulingIgnoredDuringExecution:
            - weight: 100
              podAffinityTerm:
                labelSelector:
                  matchExpressions:
                    - key: app
                      operator: In
                      values:
                        - oauth-app
                topologyKey: kubernetes.io/hostname

      containers:
        - name: oauth-app
          image: your-registry/oauth-app:1.0.0
          imagePullPolicy: IfNotPresent

          ports:
            - name: http
              containerPort: 8080
              protocol: TCP

          env:
            - name: SPRING_PROFILES_ACTIVE
              value: "production"

            - name: JAVA_OPTS
              value: >-
                -Xms1g
                -Xmx2g
                -XX:+UseG1GC
                -XX:MaxGCPauseMillis=200
                -XX:+HeapDumpOnOutOfMemoryError
                -XX:HeapDumpPath=/app/logs

            # OAuth Provider Credentials
            - name: NAVER_CLIENT_ID
              valueFrom:
                secretKeyRef:
                  name: oauth-app-secrets
                  key: naver-client-id
            - name: NAVER_CLIENT_SECRET
              valueFrom:
                secretKeyRef:
                  name: oauth-app-secrets
                  key: naver-client-secret
            - name: KAKAO_CLIENT_ID
              valueFrom:
                secretKeyRef:
                  name: oauth-app-secrets
                  key: kakao-client-id
            - name: KAKAO_CLIENT_SECRET
              valueFrom:
                secretKeyRef:
                  name: oauth-app-secrets
                  key: kakao-client-secret
            - name: GOOGLE_CLIENT_ID
              valueFrom:
                secretKeyRef:
                  name: oauth-app-secrets
                  key: google-client-id
            - name: GOOGLE_CLIENT_SECRET
              valueFrom:
                secretKeyRef:
                  name: oauth-app-secrets
                  key: google-client-secret
            - name: FACEBOOK_CLIENT_ID
              valueFrom:
                secretKeyRef:
                  name: oauth-app-secrets
                  key: facebook-client-id
            - name: FACEBOOK_CLIENT_SECRET
              valueFrom:
                secretKeyRef:
                  name: oauth-app-secrets
                  key: facebook-client-secret

            # Redis
            - name: REDIS_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: oauth-app-secrets
                  key: redis-password

          resources:
            requests:
              memory: "1Gi"
              cpu: "500m"
            limits:
              memory: "2Gi"
              cpu: "1000m"

          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            initialDelaySeconds: 30
            periodSeconds: 10
            timeoutSeconds: 5
            failureThreshold: 3

          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            initialDelaySeconds: 10
            periodSeconds: 5
            timeoutSeconds: 3
            failureThreshold: 3

          volumeMounts:
            - name: config
              mountPath: /app/config
              readOnly: true
            - name: logs
              mountPath: /app/logs

      volumes:
        - name: config
          configMap:
            name: oauth-app-config
        - name: logs
          emptyDir: {}

      terminationGracePeriodSeconds: 40
```

### Service

**service.yaml:**

```yaml
apiVersion: v1
kind: Service
metadata:
  name: oauth-app-service
  namespace: oauth-app
  labels:
    app: oauth-app
spec:
  type: ClusterIP
  selector:
    app: oauth-app
  ports:
    - name: http
      port: 80
      targetPort: 8080
      protocol: TCP
  sessionAffinity: None
```

### Ingress

**ingress.yaml:**

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: oauth-app-ingress
  namespace: oauth-app
  annotations:
    kubernetes.io/ingress.class: "nginx"
    cert-manager.io/cluster-issuer: "letsencrypt-prod"
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
    nginx.ingress.kubernetes.io/force-ssl-redirect: "true"
    nginx.ingress.kubernetes.io/proxy-body-size: "1m"
    nginx.ingress.kubernetes.io/proxy-connect-timeout: "5"
    nginx.ingress.kubernetes.io/proxy-send-timeout: "60"
    nginx.ingress.kubernetes.io/proxy-read-timeout: "60"
spec:
  tls:
    - hosts:
        - oauth.yourdomain.com
      secretName: oauth-app-tls
  rules:
    - host: oauth.yourdomain.com
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: oauth-app-service
                port:
                  number: 80
```

### HorizontalPodAutoscaler

**hpa.yaml:**

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: oauth-app-hpa
  namespace: oauth-app
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: oauth-app
  minReplicas: 3
  maxReplicas: 10
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 70
    - type: Resource
      resource:
        name: memory
        target:
          type: Utilization
          averageUtilization: 80
  behavior:
    scaleUp:
      stabilizationWindowSeconds: 60
      policies:
        - type: Percent
          value: 50
          periodSeconds: 60
    scaleDown:
      stabilizationWindowSeconds: 300
      policies:
        - type: Percent
          value: 10
          periodSeconds: 60
```

### Redis StatefulSet

**redis-statefulset.yaml:**

```yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: redis
  namespace: oauth-app
spec:
  serviceName: redis-service
  replicas: 1
  selector:
    matchLabels:
      app: redis
  template:
    metadata:
      labels:
        app: redis
    spec:
      containers:
        - name: redis
          image: redis:7.2-alpine
          command:
            - redis-server
            - --requirepass
            - $(REDIS_PASSWORD)
            - --maxmemory
            - 512mb
            - --maxmemory-policy
            - allkeys-lru
          env:
            - name: REDIS_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: oauth-app-secrets
                  key: redis-password
          ports:
            - containerPort: 6379
              name: redis
          resources:
            requests:
              memory: "256Mi"
              cpu: "250m"
            limits:
              memory: "512Mi"
              cpu: "500m"
          volumeMounts:
            - name: redis-data
              mountPath: /data
          livenessProbe:
            exec:
              command:
                - redis-cli
                - ping
            initialDelaySeconds: 30
            periodSeconds: 10
          readinessProbe:
            exec:
              command:
                - redis-cli
                - ping
            initialDelaySeconds: 5
            periodSeconds: 5
  volumeClaimTemplates:
    - metadata:
        name: redis-data
      spec:
        accessModes: ["ReadWriteOnce"]
        resources:
          requests:
            storage: 10Gi
---
apiVersion: v1
kind: Service
metadata:
  name: redis-service
  namespace: oauth-app
spec:
  clusterIP: None
  selector:
    app: redis
  ports:
    - port: 6379
      targetPort: 6379
```

### Deploy to Kubernetes

```bash
# Create namespace
kubectl apply -f k8s/namespace.yaml

# Create secrets
kubectl apply -f k8s/secret.yaml

# Create ConfigMap
kubectl apply -f k8s/configmap.yaml

# Deploy Redis
kubectl apply -f k8s/redis-statefulset.yaml

# Deploy application
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
kubectl apply -f k8s/ingress.yaml
kubectl apply -f k8s/hpa.yaml

# Verify deployment
kubectl get pods -n oauth-app
kubectl get svc -n oauth-app
kubectl get ingress -n oauth-app

# Check logs
kubectl logs -f deployment/oauth-app -n oauth-app

# Check health
kubectl exec -it deployment/oauth-app -n oauth-app -- \
  curl http://localhost:8080/actuator/health
```

---

## CI/CD Pipeline

### GitHub Actions

**.github/workflows/deploy.yml:**

```yaml
name: Deploy to Production

on:
  push:
    branches:
      - main
    tags:
      - 'v*'
  workflow_dispatch:

env:
  REGISTRY: ghcr.io
  IMAGE_NAME: ${{ github.repository }}

jobs:
  build:
    runs-on: ubuntu-latest
    permissions:
      contents: read
      packages: write

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: 'maven'

      - name: Build with Maven
        run: mvn clean package -DskipTests -B

      - name: Run tests
        run: mvn test -B

      - name: Generate code coverage
        run: mvn jacoco:report

      - name: Upload coverage to Codecov
        uses: codecov/codecov-action@v3
        with:
          files: ./target/site/jacoco/jacoco.xml

      - name: Log in to Container Registry
        uses: docker/login-action@v3
        with:
          registry: ${{ env.REGISTRY }}
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}

      - name: Extract metadata
        id: meta
        uses: docker/metadata-action@v5
        with:
          images: ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}
          tags: |
            type=ref,event=branch
            type=semver,pattern={{version}}
            type=semver,pattern={{major}}.{{minor}}
            type=sha

      - name: Build and push Docker image
        uses: docker/build-push-action@v5
        with:
          context: .
          push: true
          tags: ${{ steps.meta.outputs.tags }}
          labels: ${{ steps.meta.outputs.labels }}

      - name: Upload artifact
        uses: actions/upload-artifact@v4
        with:
          name: oauth-app-jar
          path: target/*.jar

  deploy:
    needs: build
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Configure kubectl
        uses: azure/k8s-set-context@v3
        with:
          method: kubeconfig
          kubeconfig: ${{ secrets.KUBE_CONFIG }}

      - name: Deploy to Kubernetes
        run: |
          kubectl set image deployment/oauth-app \
            oauth-app=${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}:sha-${{ github.sha }} \
            -n oauth-app

          kubectl rollout status deployment/oauth-app -n oauth-app --timeout=5m

      - name: Verify deployment
        run: |
          kubectl get pods -n oauth-app
          kubectl get svc -n oauth-app

      - name: Run smoke tests
        run: |
          kubectl wait --for=condition=ready pod \
            -l app=oauth-app \
            -n oauth-app \
            --timeout=5m

          ENDPOINT=$(kubectl get ingress oauth-app-ingress -n oauth-app \
            -o jsonpath='{.status.loadBalancer.ingress[0].hostname}')

          curl -f https://$ENDPOINT/actuator/health || exit 1
```

### GitLab CI

**.gitlab-ci.yml:**

```yaml
stages:
  - build
  - test
  - package
  - deploy

variables:
  MAVEN_OPTS: "-Dmaven.repo.local=$CI_PROJECT_DIR/.m2/repository"
  DOCKER_IMAGE: $CI_REGISTRY_IMAGE:$CI_COMMIT_SHORT_SHA

cache:
  paths:
    - .m2/repository

build:
  stage: build
  image: maven:3.9.6-eclipse-temurin-21
  script:
    - mvn clean compile -B
  artifacts:
    paths:
      - target/

test:
  stage: test
  image: maven:3.9.6-eclipse-temurin-21
  script:
    - mvn test -B
    - mvn jacoco:report
  coverage: '/Total.*?([0-9]{1,3})%/'
  artifacts:
    reports:
      junit: target/surefire-reports/TEST-*.xml
      coverage_report:
        coverage_format: cobertura
        path: target/site/jacoco/jacoco.xml

package:
  stage: package
  image: maven:3.9.6-eclipse-temurin-21
  script:
    - mvn package -DskipTests -B
  artifacts:
    paths:
      - target/*.jar

docker:
  stage: package
  image: docker:latest
  services:
    - docker:dind
  before_script:
    - docker login -u $CI_REGISTRY_USER -p $CI_REGISTRY_PASSWORD $CI_REGISTRY
  script:
    - docker build -t $DOCKER_IMAGE .
    - docker push $DOCKER_IMAGE
    - docker tag $DOCKER_IMAGE $CI_REGISTRY_IMAGE:latest
    - docker push $CI_REGISTRY_IMAGE:latest

deploy:production:
  stage: deploy
  image: bitnami/kubectl:latest
  only:
    - main
  script:
    - kubectl config use-context production
    - kubectl set image deployment/oauth-app oauth-app=$DOCKER_IMAGE -n oauth-app
    - kubectl rollout status deployment/oauth-app -n oauth-app --timeout=5m
  environment:
    name: production
    url: https://oauth.yourdomain.com
```

---

## Zero-Downtime Deployment

### Rolling Update Strategy

**Kubernetes Rolling Update:**

```yaml
spec:
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1          # Create 1 extra pod during update
      maxUnavailable: 0    # Keep all pods available
```

**Deployment Process:**

1. Create new pod with new version
2. Wait for pod to become ready
3. Terminate one old pod
4. Repeat until all pods updated

### Blue-Green Deployment

**Blue (Current) Deployment:**

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: oauth-app-blue
  labels:
    app: oauth-app
    version: blue
spec:
  replicas: 3
  selector:
    matchLabels:
      app: oauth-app
      version: blue
  template:
    metadata:
      labels:
        app: oauth-app
        version: blue
    spec:
      containers:
        - name: oauth-app
          image: oauth-app:1.0.0
```

**Green (New) Deployment:**

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: oauth-app-green
  labels:
    app: oauth-app
    version: green
spec:
  replicas: 3
  selector:
    matchLabels:
      app: oauth-app
      version: green
  template:
    metadata:
      labels:
        app: oauth-app
        version: green
    spec:
      containers:
        - name: oauth-app
          image: oauth-app:1.1.0
```

**Service Switching:**

```bash
# Initially pointing to blue
kubectl patch service oauth-app-service -p '{"spec":{"selector":{"version":"blue"}}}'

# Deploy green
kubectl apply -f deployment-green.yaml

# Wait for green to be ready
kubectl wait --for=condition=available deployment/oauth-app-green --timeout=5m

# Smoke test green
kubectl port-forward deployment/oauth-app-green 8080:8080
curl http://localhost:8080/actuator/health

# Switch traffic to green
kubectl patch service oauth-app-service -p '{"spec":{"selector":{"version":"green"}}}'

# Monitor for issues
# If issues, switch back to blue:
# kubectl patch service oauth-app-service -p '{"spec":{"selector":{"version":"blue"}}}'

# After verification, delete blue
kubectl delete deployment oauth-app-blue
```

### Canary Deployment

**Main Deployment (90% traffic):**

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: oauth-app-main
spec:
  replicas: 9
  selector:
    matchLabels:
      app: oauth-app
      track: stable
```

**Canary Deployment (10% traffic):**

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: oauth-app-canary
spec:
  replicas: 1
  selector:
    matchLabels:
      app: oauth-app
      track: canary
```

**Service (load balances across both):**

```yaml
apiVersion: v1
kind: Service
metadata:
  name: oauth-app-service
spec:
  selector:
    app: oauth-app  # Matches both main and canary
  ports:
    - port: 80
      targetPort: 8080
```

---

## Rollback Strategy

### Kubernetes Rollback

**Check rollout history:**

```bash
kubectl rollout history deployment/oauth-app -n oauth-app
```

**Rollback to previous version:**

```bash
kubectl rollout undo deployment/oauth-app -n oauth-app
```

**Rollback to specific revision:**

```bash
kubectl rollout undo deployment/oauth-app --to-revision=2 -n oauth-app
```

**Monitor rollback:**

```bash
kubectl rollout status deployment/oauth-app -n oauth-app
```

### Docker Rollback

**List image tags:**

```bash
docker images oauth-app
```

**Revert to previous tag:**

```bash
docker tag oauth-app:1.0.0 oauth-app:latest
docker-compose up -d
```

### Database Rollback (If Applicable)

**Not applicable for sb-oauth-java:**
- No database schema changes
- State stored in Redis (ephemeral)
- Configuration-driven

### Rollback Checklist

**Before Rollback:**
- [ ] Identify the issue
- [ ] Check logs and metrics
- [ ] Notify stakeholders
- [ ] Document the problem

**During Rollback:**
- [ ] Execute rollback command
- [ ] Monitor deployment status
- [ ] Verify health checks pass
- [ ] Test critical flows

**After Rollback:**
- [ ] Verify service is working
- [ ] Check error rates returned to normal
- [ ] Root cause analysis
- [ ] Update documentation
- [ ] Create fix plan

---

## Deployment Checklist

### Pre-Deployment

**Code:**
- [ ] All tests passing
- [ ] Code review completed
- [ ] Security scan passed
- [ ] Performance benchmarks acceptable

**Configuration:**
- [ ] Environment variables set
- [ ] Secrets configured
- [ ] ConfigMaps updated
- [ ] Resource limits defined

**Infrastructure:**
- [ ] Kubernetes cluster ready
- [ ] Load balancer configured
- [ ] DNS records updated
- [ ] SSL certificates valid

**Monitoring:**
- [ ] Monitoring dashboards ready
- [ ] Alerts configured
- [ ] Logs aggregation setup
- [ ] On-call engineer notified

### Deployment

- [ ] Deploy to staging first
- [ ] Run smoke tests
- [ ] Monitor metrics
- [ ] Check logs for errors
- [ ] Verify health checks
- [ ] Test OAuth flows

### Post-Deployment

- [ ] Verify all pods running
- [ ] Check error rates
- [ ] Monitor performance
- [ ] Test from external network
- [ ] Update documentation
- [ ] Notify stakeholders

---

## Support

For deployment issues:
1. Check this guide
2. Review [PRODUCTION_GUIDE.md](PRODUCTION_GUIDE.md)
3. Consult [SECURITY.md](SECURITY.md)
4. Contact DevOps team

**Emergency Rollback:**
```bash
kubectl rollout undo deployment/oauth-app -n oauth-app
```

---

**Last Updated:** 2024-11-19
**Version:** 1.0.0
