package aivle.project.operation.infra.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {
    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.expiredMs}")
    private Long expirationTime;

    private Key getKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public boolean isExpired(String token){
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            log.info("Valid token: {}-{}", claims.get("role"), claims.get("id"));
            return false;

        } catch (ExpiredJwtException e) {
            log.info("Token expired: {}", e.getMessage());
        } catch (JwtException e) {
            log.info("Token forgery or other errors: {}", e.getMessage());
        }
        return true;
    }

    public String createToken(String role, Long id, String name, String taskType) {
        Claims claims = Jwts.claims();
        claims.put("role", role);
        claims.put("id", id);
        claims.put("name", name);
        claims.put("taskType", taskType);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
