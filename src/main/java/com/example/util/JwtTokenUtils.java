package com.example.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
@Getter
@Setter
public class JwtTokenUtils {

    @Value("${jwt.secret-key}")
    private String key;

    @Value("${jwt.token.expired-time-ms}")
    private Long expiredTimeMs;

    public  String getUserName(String token){
        return extractClaims(token).get("userName", String.class);
    }

    public  boolean isExpired(String token){
        Date expiredDate = extractClaims(token).getExpiration();
        return expiredDate.before(new Date());
    }

    private  Claims extractClaims(String token){
        return Jwts.parserBuilder().setSigningKey(convertToByteKey(key))
                .build().parseClaimsJws(token).getBody();
    }

    public  String generateToken(String userName){
        Claims claims = Jwts.claims();
        claims.put("userName",userName);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+ expiredTimeMs))
                .signWith(convertToByteKey(key), SignatureAlgorithm.HS256)
                .compact()
                ;
    }

    private Key convertToByteKey(String key){
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
