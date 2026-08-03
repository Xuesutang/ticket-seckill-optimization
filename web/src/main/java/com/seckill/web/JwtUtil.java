package com.seckill.web;
import io.jsonwebtoken.*; import io.jsonwebtoken.security.Keys; import org.springframework.beans.factory.annotation.Value; import org.springframework.stereotype.Component;
import javax.crypto.SecretKey; import java.nio.charset.StandardCharsets; import java.util.Date;
@Component public class JwtUtil {
 private final SecretKey key; public JwtUtil(@Value("${security.jwt.secret}") String secret){this.key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));}
 public String create(Long userId){return Jwts.builder().setSubject(String.valueOf(userId)).setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis()+86400000L)).signWith(key).compact();}
 public Long userId(String token){return Long.valueOf(Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject());}
}
