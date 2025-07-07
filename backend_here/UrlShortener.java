package com.example.MED.backend;

import static spark.Spark.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.ClassicHttpResponse;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UrlShortener {
    private static final Map<String, ShortLink> store = new ConcurrentHashMap<>();
    private static final Random random = new Random();
    static class LoggingMiddleware {
        private final String logApiUrl;
        private final String accessToken;

        public LoggingMiddleware(String logApiUrl, String accessToken) {
            this.logApiUrl = logApiUrl;
            this.accessToken = accessToken;
        }

        public void log(String stack, String level, String pkg, String message) throws Exception {
            Map<String, String> payload = new HashMap<>();
            payload.put("stack", stack);
            payload.put("level", level);
            payload.put("package", pkg);
            payload.put("message", message);

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(payload);

            HttpClient client = HttpClients.createDefault();
            HttpPost post = new HttpPost(logApiUrl);
            post.setHeader("Content-Type", "application/json");
            post.setHeader("Authorization", "Bearer " + accessToken);
            post.setEntity(new StringEntity(json));

            try (ClassicHttpResponse response = (ClassicHttpResponse) client.execute(post)) {
                // Optionally handle response
            }
        }
    }
    static class ShortLink {
        public final String longUrl;
        public final long expiryTime;

        public ShortLink(String longUrl, long expiryTime) {
            this.longUrl = longUrl;
            this.expiryTime = expiryTime;
        }
    }

    private static String generateCode() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String code;
        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 6; i++) sb.append(chars.charAt(random.nextInt(chars.length())));
            code = sb.toString();
        } while (store.containsKey(code));
        return code;
    }

    public static void main(String[] args) throws Exception {
        String logApiUrl = "http://20.244.56.144/evaluation-service/log";
        String accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJNYXBDbGFpbXMiOnsiYXVkIjoiaHR0cDovLzIwLjI0NC41Ni4xNDQvZXZhbHVhdGlvbi1zZXJ2aWNlIiwiZW1haWwiOiIyMDIyaXQwMDkzQHN2Y2UuYWMuaW4iLCJleHAiOjE3NTE4ODE2MTYsImlhdCI6MTc1MTg4MDcxNiwiaXNzIjoiQWZmb3JkIE1lZGljYWwgVGVjaG5vbG9naWVzIFByaXZhdGUgTGltaXRlZCIsImp0aSI6IjM3YTUwMjgyLTZjOGQtNDBlZi04MzQ5LTJjNWFjMDRlY2RiOCIsImxvY2FsZSI6ImVuLUlOIiwibmFtZSI6InJvaGl0aCBrdW1hYXIgcCIsInN1YiI6Ijc1NzY0ZjM4LWZkNDQtNDJhNS1hYWNiLWQzODQ2ODE5YzY5MCJ9LCJlbWFpbCI6IjIwMjJpdDAwOTNAc3ZjZS5hYy5pbiIsIm5hbWUiOiJyb2hpdGgga3VtYWFyIHAiLCJyb2xsTm8iOiIyMTI3MjIwODAxMDcyIiwiYWNjZXNzQ29kZSI6IlpSc1lYeCIsImNsaWVudElEIjoiNzU3NjRmMzgtZmQ0NC00MmE1LWFhY2ItZDM4NDY4MTljNjkwIiwiY2xpZW50U2VjcmV0IjoiQWFnVEdEVHNGUXFyWWh3WiJ9.PM7o8MjOhvjrDGTnHRyY1JVtGNNxX-9LNg7ZqglvKCQ";

        LoggingMiddleware logger = new LoggingMiddleware(logApiUrl, accessToken);
        ObjectMapper mapper = new ObjectMapper();

        port(4567);

        post("/shorten", (req, res) -> {
            Map<String, Object> body = mapper.readValue(req.body(), Map.class);
            String longUrl = (String) body.get("longUrl");
            String customCode = (String) body.get("customCode");
            Integer validity = body.get("validityMinutes") != null ? (Integer) body.get("validityMinutes") : null;

            if (longUrl == null || longUrl.isEmpty()) {
                res.status(400);
                logger.log("backend", "error", "shortener", "Missing longUrl");
                return "{\"error\":\"Missing longUrl\"}";
            }

            String code = customCode != null && !customCode.isEmpty() ? customCode : generateCode();
            if (store.containsKey(code)) {
                res.status(409);
                logger.log("backend", "error", "shortener", "Shortcode collision");
                return "{\"error\":\"Shortcode collision\"}";
            }
            long expiry = System.currentTimeMillis() + 60000L * (validity != null ? validity : 30);
            store.put(code, new ShortLink(longUrl, expiry));
            res.status(201);
            logger.log("backend", "info", "shortener", "Short link created: " + code);
            return "{\"shortUrl\":\"http://localhost:4567/" + code + "\"}";
        });

        get("/:code", (req, res) -> {
            String code = req.params(":code");
            ShortLink link = store.get(code);
            if (link == null) {
                res.status(404);
                logger.log("backend", "error", "shortener", "Shortcode not found: " + code);
                return "{\"error\":\"Shortcode not found\"}";
            }
            if (System.currentTimeMillis() > link.expiryTime) {
                res.status(410);
                logger.log("backend", "error", "shortener", "Link expired: " + code);
                return "{\"error\":\"Link expired\"}";
            }
            res.redirect(link.longUrl);
            logger.log("backend", "info", "shortener", "Redirected: " + code);
            return null;
        });
    }
}