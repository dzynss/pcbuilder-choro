package com.pcbuilder.ms_despachos.security;

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
 * Filtro que se ejecuta una vez por request (OncePerRequestFilter) para validar el JWT emitido por ms_login.
 * Registrado por SecurityConfig antes de UsernamePasswordAuthenticationFilter en la cadena de seguridad.
 * No consulta a ningún microservicio: valida la firma localmente usando jwt.secret (misma clave que ms_login).
 */
@Component
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final Key llaveSecreta;

    /** Deriva la clave HMAC de firma a partir de la property jwt.secret. */
    public JwtAuthFilter(@Value("${jwt.secret}") String secreto) {
        this.llaveSecreta = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Si viene header "Authorization: Bearer <token>" y es válido, autentica al usuario (correo como principal)
     * en el SecurityContext; si el token es inválido/expirado, limpia el contexto y deja pasar la request
     * (SecurityConfig.filterChain exige autenticación salvo en rutas de swagger, por lo que terminará en 401).
     */
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
