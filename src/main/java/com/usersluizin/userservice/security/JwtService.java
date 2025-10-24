package com.usersluizin.userservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final Key signingKey;
    private final long expirationMinutes;

    public JwtService(@Value("${security.jwt.secret}") String secret,
                      @Value("${security.jwt.expiration-minutes:60}") long expirationMinutes) {
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expirationMinutes = expirationMinutes;
    }

    public String generateToken(ApplicationUserDetails userDetails) {
        Instant now = Instant.now();
        Instant expiration = now.plus(expirationMinutes, ChronoUnit.MINUTES);
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .addClaims(Map.of(
                        "role", userDetails.getUser().getRole().name(),
                        "active", userDetails.getUser().isActive()
                ))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, ApplicationUserDetails userDetails) {
        String username = extractClaim(token).getSubject();
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public Claims extractClaim(String token) {
        return Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token).getBody();
    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractClaim(token).getExpiration();
        return expiration.before(new Date());
    }
}
