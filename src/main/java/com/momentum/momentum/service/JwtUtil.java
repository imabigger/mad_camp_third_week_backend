package com.momentum.momentum.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    private final String SECRET_KEY = EnvUtil.getEnv("JWT_SECRET_KEY");  // 최소 32바이트 길이로 설정
    private final SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));

    // Access Token 유효 기간: 10시간
    private final long ACCESS_TOKEN_VALIDITY = 1000 * 60 * 60 * 10;
    // Refresh Token 유효 기간: 7일
    private final long REFRESH_TOKEN_VALIDITY = 1000 * 60 * 60 * 24 * 7;

    public String generateToken(String userId) {
        return createToken(new HashMap<>(), userId, ACCESS_TOKEN_VALIDITY);
    }

    public String generateRefreshToken(String userId) {
        return createToken(new HashMap<>(), userId, REFRESH_TOKEN_VALIDITY);
    }

    private String createToken(Map<String, Object> claims, String subject, long validity) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject) // 여기서 subject에 사용자 이름을 설정
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + validity))
                .signWith(key)
                .compact();
    }

    public Boolean validateToken(String token, String userId) {
        try {
            final String extractedUserId = extractUserId(token);
            return (extractedUserId.equals(userId) && !isTokenExpired(token));
        } catch (ExpiredJwtException e) {
            throw new JwtTokenExpiredException("JWT token has expired");
        }
    }

    public Boolean validateRefreshToken(String token) {
        return !isTokenExpired(token);
    }

    public String extractUserId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseSignedClaims(token)
                .getBody();
    }

    public class JwtTokenExpiredException extends RuntimeException {
        public JwtTokenExpiredException(String message) {
            super(message);
        }
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}