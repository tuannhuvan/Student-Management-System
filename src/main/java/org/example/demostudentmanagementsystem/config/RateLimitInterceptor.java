package org.example.demostudentmanagementsystem.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.demostudentmanagementsystem.exception.RateLimitExceededException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Spring MVC Interceptor enforcing Token Bucket rate limiting on user registration endpoints.
 */
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private static final int CAPACITY = 5;
    private static final int REFILL_TOKENS = 5;
    private static final long REFILL_DURATION_MS = 60_000L; // 1 minute
    private static final long CLEANUP_TTL_MS = 10 * 60_000L; // 10 minutes TTL

    private final Map<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    /**
     * Intercepts incoming HTTP requests to check token bucket availability for the client IP.
     *
     * @param request  Current HTTP request
     * @param response Current HTTP response
     * @param handler  Chosen handler to execute
     * @return true if request is permitted, throws RateLimitExceededException if rate limit is exceeded
     */
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        cleanupStaleBuckets();
        String clientIp = extractClientIp(request);
        TokenBucket bucket = buckets.computeIfAbsent(clientIp, k -> new TokenBucket(CAPACITY, REFILL_TOKENS, REFILL_DURATION_MS));

        if (!bucket.tryConsume()) {
            response.setHeader("Retry-After", "60");
            throw new RateLimitExceededException("Rate limit exceeded. Maximum 5 registration attempts allowed per minute.");
        }
        return true;
    }

    private String extractClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void cleanupStaleBuckets() {
        long now = System.currentTimeMillis();
        buckets.entrySet().removeIf(entry -> now - entry.getValue().getLastAccessTime() > CLEANUP_TTL_MS);
    }

    /**
     * Inner thread-safe Token Bucket implementation.
     */
    private static class TokenBucket {
        private final int capacity;
        private final int refillTokens;
        private final long refillIntervalMs;
        private double tokens;
        private long lastRefillTime;
        private long lastAccessTime;

        public TokenBucket(int capacity, int refillTokens, long refillIntervalMs) {
            this.capacity = capacity;
            this.refillTokens = refillTokens;
            this.refillIntervalMs = refillIntervalMs;
            this.tokens = capacity;
            long now = System.currentTimeMillis();
            this.lastRefillTime = now;
            this.lastAccessTime = now;
        }

        public synchronized boolean tryConsume() {
            refill();
            this.lastAccessTime = System.currentTimeMillis();
            if (tokens >= 1) {
                tokens -= 1;
                return true;
            }
            return false;
        }

        private void refill() {
            long now = System.currentTimeMillis();
            long elapsed = now - lastRefillTime;
            if (elapsed > 0) {
                double addedTokens = (double) elapsed * refillTokens / refillIntervalMs;
                tokens = Math.min(capacity, tokens + addedTokens);
                lastRefillTime = now;
            }
        }

        public long getLastAccessTime() {
            return lastAccessTime;
        }
    }
}
