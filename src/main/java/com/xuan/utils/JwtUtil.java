package com.xuan.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "223344aaab2223333bcbcbc444555666cccawes"; // 替换为您的密钥
    private final long EXPIRATION_TIME = 1000 * 60 * 60; // 1小时

    // 生成JWT，携带用户名和用户ID
    public String generateToken(String username, String userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("userId", userId); // 添加用户ID
        JwtBuilder builder = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        return builder.compact();
    }

    // 验证JWT
    public boolean validateToken(String token, String username) {
        String parsedUsername = getUsernameFromToken(token);
        return (parsedUsername.equals(username) && !isTokenExpired(token));
    }

    // 从JWT获取用户名
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token)
                .getBody();
        return claims.get("username", String.class);
    }

    // 从JWT获取用户ID
    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token)
                .getBody();
        return claims.get("userId", String.class); // 返回用户ID
    }

    // 检查token是否过期
    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parser()
                .setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }
}
