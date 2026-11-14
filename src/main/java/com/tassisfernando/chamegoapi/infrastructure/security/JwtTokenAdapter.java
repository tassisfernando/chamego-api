package com.tassisfernando.chamegoapi.infrastructure.security;

import com.tassisfernando.chamegoapi.application.auth.GeradorTokenPort;
import com.tassisfernando.chamegoapi.domain.usuario.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtTokenAdapter implements GeradorTokenPort {

    private final SecretKey chave;
    private final long expirationMs;

    public JwtTokenAdapter(
            @Value("${chamego.jwt.secret}") String secret,
            @Value("${chamego.jwt.expiration-ms}") long expirationMs) {
        this.chave = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    @Override
    public String gerar(Usuario usuario) {
        Instant agora = Instant.now();
        Instant expira = agora.plusMillis(expirationMs);
        return Jwts.builder()
                .subject(usuario.login())
                .claim("uid", usuario.id().toString())
                .issuedAt(Date.from(agora))
                .expiration(Date.from(expira))
                .signWith(chave)
                .compact();
    }

    @Override
    public Instant expiracaoDe(String token) {
        return extrairClaims(token).getExpiration().toInstant();
    }

    @Override
    public String extrairLogin(String token) {
        return extrairClaims(token).getSubject();
    }

    @Override
    public boolean valido(String token) {
        try {
            extrairClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private Claims extrairClaims(String token) {
        return Jwts.parser()
                .verifyWith(chave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
