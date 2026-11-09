package ucr.ac.cr.medivault_security_backend.security;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final int LOGIN_LIMIT_PER_MINUTE = 5;
    private static final int API_LIMIT_PER_MINUTE = 30;

    private final Map<String, Bucket> loginBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> apiBuckets = new ConcurrentHashMap<>();

    private Bucket createBucket(int capacity) {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(capacity,
                        Refill.intervally(capacity, Duration.ofMinutes(1))))
                .build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String clientIp = ClientIpResolver.resolve(request);
        Bucket target = null;

        if (path.startsWith("/api/v1/auth/login")) {
            target = loginBuckets.computeIfAbsent(clientIp, ip -> createBucket(LOGIN_LIMIT_PER_MINUTE));
        } else if (path.startsWith("/api/v1/expedientes")) {
            target = apiBuckets.computeIfAbsent(clientIp, ip -> createBucket(API_LIMIT_PER_MINUTE));
        }

        if (target == null || target.tryConsume(1)) {
            filterChain.doFilter(request, response);
            return;
        }

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.setHeader("Retry-After", "60");

        String json = """
                {
                  "type": "%s",
                  "title": "%s",
                  "status": 429,
                  "detail": "%s",
                  "instance": "%s"
                }
                """.formatted(
                "https://medivault.cr/rl",
                "Too Many Requests",
                "Limit exceeded. Wait 60s.",
                path
        );
        response.getWriter().write(json);
    }
}