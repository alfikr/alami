package com.jasavast.core.security.filter;

import io.jsonwebtoken.*;
import io.jsonwebtoken.jackson.io.JacksonSerializer;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;
@Configuration
@Slf4j
public class TokenProvider {
    private static final String AUTHORITIES_KEY="auth";
    private Key key;
    @Value("${application.security.validasi}")
    private long tokenValidityInMilliseconds=1800;
    @Value("${application.security.remember-me}")
    private long tokenValidityInMillisecondsForRememberMe;

    @Value("${application.security.refreshDateInMs}")
    private long refreshExpirationDateInMs;
    @Value("${application.security.secret}")
    private String secret;

    private JwtParser jwtParser;
    @PostConstruct
    public void init() {
        byte[] keyBytes=secret.getBytes();
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.tokenValidityInMilliseconds =
                1000 * tokenValidityInMilliseconds;
        this.tokenValidityInMillisecondsForRememberMe =
                1000 * tokenValidityInMillisecondsForRememberMe;
        this.jwtParser=Jwts.parserBuilder()
                .setSigningKey(this.key).build();
    }

    public String createToken(Authentication authentication, boolean rememberMe) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity;
        if (rememberMe) {
            validity = new Date(now + this.tokenValidityInMillisecondsForRememberMe);
        } else {
            validity = new Date(now + this.tokenValidityInMilliseconds);
        }

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .serializeToJsonWith(new JacksonSerializer())
                .compact();
    }
    public String refreshToken(String subject, Map claims){
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+refreshExpirationDateInMs))
                .signWith(key).compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String authToken) {
        try {
            jwtParser.parseClaimsJws(authToken);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("jwt expired {}",e);
        } catch (UnsupportedJwtException e) {

            log.error("jwt unsupported", e);
        } catch (MalformedJwtException e) {

            log.error("invalid jwt token", e);
        } catch (IllegalArgumentException e) {
            log.error("Token validation error {}", e.getMessage());
        }

        return false;
    }
}
