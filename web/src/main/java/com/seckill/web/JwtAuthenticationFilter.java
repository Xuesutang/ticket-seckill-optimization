package com.seckill.web;
import io.jsonwebtoken.JwtException; import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; import org.springframework.security.core.authority.SimpleGrantedAuthority; import org.springframework.security.core.context.SecurityContextHolder; import org.springframework.stereotype.Component; import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.*; import javax.servlet.http.*; import java.io.IOException; import java.util.Collections;
@Component public class JwtAuthenticationFilter extends OncePerRequestFilter {
 private final JwtUtil jwt; public JwtAuthenticationFilter(JwtUtil jwt){this.jwt=jwt;}
 @Override protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain)throws ServletException,IOException {String header=req.getHeader("Authorization"); if(header!=null&&header.startsWith("Bearer ")){try{Long userId=jwt.userId(header.substring(7)); SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userId,null,Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))));}catch(JwtException|IllegalArgumentException ignored){}} chain.doFilter(req,res);}
}
