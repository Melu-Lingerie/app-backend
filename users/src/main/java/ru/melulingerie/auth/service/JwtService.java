package ru.melulingerie.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.melulingerie.users.entity.User;
import ru.melulingerie.users.entity.UserCredentials;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-token.expiration:900}")     // 15 минут по умолчанию
    private long accessTtlSeconds;

    @Value("${jwt.refresh-token.expiration:2592000}") // 30 дней по умолчанию
    private long refreshTtlSeconds;

    private SecretKey key() {
        // HS256/HS512 ключ не короче 256 bit
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String extractEmail(String token) {
        // subject = email (на этапе генерации позже это зафиксируем)
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isTokenExpired(String token) {
        Date exp = extractExpiration(token);
        return exp != null && exp.before(new Date());
    }

    public boolean isValid(String token) {
        try {
            // Если распарсили и не истёк — считаем валидным (минимальная проверка)
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String generateAccessToken(User user, UserCredentials creds) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("role", user.getRole().name());
        claims.put("type", "ACCESS");
        // Дополнительно можно добавить статус и имя по необходимости

        return buildToken(claims, creds.getIdentifier(), accessTtlSeconds);
    }

    public String generateRefreshToken(User user, UserCredentials creds) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("type", "REFRESH");

        return buildToken(claims, creds.getIdentifier(), refreshTtlSeconds);
    }

    private String buildToken(Map<String, Object> claims, String subject, long ttlSeconds) {
        Instant now = Instant.now();
        return Jwts.builder()
                .claims(claims)
                .subject(subject)                        // email
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(ttlSeconds)))
                .signWith(key())  // JJWT 0.12.x с автоматическим алгоритмом
                .compact();
    }

    public long getAccessTtlSeconds() {
        return accessTtlSeconds;
    }

    public long getRefreshTtlSeconds() {
        return refreshTtlSeconds;
    }
}
