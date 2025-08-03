package com.interger.quizzy.config;

import com.interger.quizzy.model.User;
import com.interger.quizzy.service.MyUserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret.key:KA92Ab8OphARt/lQwY6u5Zn+LkwISP6m9ABjI3JQfVo=}")
    private String SECRET_KEY;

    @Value("${jwt.refresh.key:HyaFfpChC8IekjRGc5loPYid4/uHekm0dBmlJaYnvq0=}")
    private String REFRESH_SECRET_KEY;

    @Value("${jwt.access.expiration:5400000}") // 90 minutes (1000 * 60 * 90)
    private long ACCESS_TOKEN_EXPIRATION;

    @Value("${jwt.refresh.expiration:1728000000}") // 20 days
    private long REFRESH_TOKEN_EXPIRATION;

    /**
     * Get signing key for access tokens
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
    }
    /**
     * Get signing key for refresh tokens
     */
    private SecretKey getRefreshSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(REFRESH_SECRET_KEY));
    }

    /**
     * Generate Access Token
     */
    public String generateAccessToken(User user) {
        return generateToken(user, getSigningKey(), ACCESS_TOKEN_EXPIRATION);
    }

    /**
     * Generate Refresh Token
     */
    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", user.getUserId());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .signWith(getRefreshSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Common token generation logic
     */
    private String generateToken(User user, SecretKey key, long expiration) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("sub", user.getUserId());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extract User ID from Access Token
     */
    public String extractUserId(String token) {
        return extractClaims(token, getSigningKey()).getSubject();
    }

    /**
     * Extract User ID from Refresh Token
     */
    public String extractUserIdFromRefreshToken(String token) {
        return extractClaims(token, getRefreshSigningKey()).getSubject();
    }

    /**
     * Extract Username from Access Token (for backward compatibility)
     */
//    public String extractUserid(String token) {
//        return extractUserId(token);
//    }

    /**
     * Extract Username from Refresh Token (for backward compatibility)
     */
    public String extractUsernameFromRefreshToken(String token) {
        return extractUserIdFromRefreshToken(token);
    }

    /**
     * Validate Access Token
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            String userId = extractUserId(token);
            User user = ((MyUserPrincipal) userDetails).getUser();
            return userId.equals(user.getUserId().toString()) && !isTokenExpired(token, getSigningKey());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validate Refresh Token
     */
    public boolean validateRefreshToken(String token, UserDetails userDetails) {
        try {
            String userId = extractUserIdFromRefreshToken(token);
            User user = ((MyUserPrincipal) userDetails).getUser();
            return userId.equals(user.getUserId().toString()) && !isTokenExpired(token, getRefreshSigningKey());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if Token is Expired
     */
    private boolean isTokenExpired(String token, SecretKey key) {
        try {
            return extractClaims(token, key).getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Extract Claims from Token
     */
    private Claims extractClaims(String token, SecretKey key) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Extract expiration date from access token
     */
    public Date extractExpiration(String token) {
        return extractClaims(token, getSigningKey()).getExpiration();
    }

    /**
     * Extract expiration date from refresh token
     */
    public Date extractExpirationFromRefreshToken(String token) {
        return extractClaims(token, getRefreshSigningKey()).getExpiration();
    }

    /**
     * Check if access token is expired
     */
    public boolean isAccessTokenExpired(String token) {
        return isTokenExpired(token, getSigningKey());
    }

    /**
     * Check if refresh token is expired
     */
    public boolean isRefreshTokenExpired(String token) {
        return isTokenExpired(token, getRefreshSigningKey());
    }
}