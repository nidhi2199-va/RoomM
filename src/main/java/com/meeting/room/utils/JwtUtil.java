package com.meeting.room.utils;

import com.meeting.room.dao.UserDao;
import com.meeting.room.enums.Role;
import com.meeting.room.exceptions.UserNotFoundException;
import com.meeting.room.model.Users;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtUtil {

    @Value("${jwt.secret.key}")
    private String secretKey;
    private final UserDao userDao;

    public JwtUtil(UserDao userDao) {
        this.userDao = userDao;
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    // Generate JWT token with necessary fields (email)
    public String generateToken(String email) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        Optional<Users> user = userDao.findByEmail(email);
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .claim("role", user.get().getRole())
                .expiration(new Date(System.currentTimeMillis() + 86400000)) // 24 hours expiration
                .signWith(key)
                .compact();
    }


    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, ClaimsResolver<T> claimsResolver) {
        Claims claims = Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.resolve(claims);
    }
    // Extract the role from the token
    public Role extractRole(String token) {
        String email = extractEmail(token);
        Optional<Users> user = userDao.findByEmail(email);
        if (user.isEmpty()) {
            throw new UserNotFoundException("No user found");
        } else {
            return user.get().getRole();
        }
    }

    // Extract any claim from the token
    private Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }



    @FunctionalInterface
    public interface ClaimsResolver<T> {
        T resolve(Claims claims);
    }
}