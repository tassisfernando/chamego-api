package com.tassisfernando.chamegoapi.infrastructure.security;

import com.tassisfernando.chamegoapi.application.auth.GeradorTokenPort;
import com.tassisfernando.chamegoapi.domain.usuario.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final GeradorTokenPort geradorToken;
    private final UsuarioRepository usuarioRepository;

    public JwtAuthenticationFilter(GeradorTokenPort geradorToken, UsuarioRepository usuarioRepository) {
        this.geradorToken = geradorToken;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7).trim();
            if (geradorToken.valido(token)) {
                String login = geradorToken.extrairLogin(token);
                usuarioRepository.buscarPorLogin(login).filter(u -> u.ativo()).ifPresent(usuario -> {
                    var auth = new UsernamePasswordAuthenticationToken(
                            usuario.login(),
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                });
            }
        }
        filterChain.doFilter(request, response);
    }
}
