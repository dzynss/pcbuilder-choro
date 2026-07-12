package com.pcbuilder.ms_componentes.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;

/**
 * Filtro HTTP (uno por request) que valida el JWT emitido por ms_login en el header
 * Authorization: Bearer. Si el token es válido, autentica al usuario en el
 * SecurityContext (usando el correo como principal); si falta o es inválido, la
 * request sigue sin autenticación y {@link SecurityConfig} decide si se rechaza.
 * Se registra como filtro en la cadena de seguridad configurada en {@link SecurityConfig}.
 */
@Component
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final Key llaveSecreta;

    /** Deriva la llave HMAC de firma a partir de la propiedad jwt.secret (debe coincidir con la usada por ms_login). */
    public JwtAuthFilter(@Value("${jwt.secret}") String secreto) {
        this.llaveSecreta = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
    }

    /** Extrae y valida el JWT del header Authorization; si es válido, puebla el SecurityContext antes de continuar la cadena de filtros. */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Claims claims = Jwts.parserBuilder().setSigningKey(llaveSecreta).build()
                        .parseClaimsJws(token).getBody();
                String correo = claims.getSubject();
                var auth = new UsernamePasswordAuthenticationToken(correo, null, List.of());
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (JwtException | IllegalArgumentException e) {
                log.warn("Token JWT inválido o expirado: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }
}
