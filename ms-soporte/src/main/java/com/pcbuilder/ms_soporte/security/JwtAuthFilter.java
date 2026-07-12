package com.pcbuilder.ms_soporte.security;

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
 * Filtro de servlet que se ejecuta una vez por request (antes de UsernamePasswordAuthenticationFilter,
 * ver {@link SecurityConfig}). Lee el header Authorization: Bearer &lt;token&gt;, valida la firma
 * JWT (HS256, misma clave que usa ms_login) y, si es válido, autentica al usuario en el
 * SecurityContext usando el correo (subject del token) como principal.
 */
@Component
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final Key llaveSecreta;

    /** Deriva la clave HMAC a partir de la propiedad {@code jwt.secret} (debe coincidir con ms_login). */
    public JwtAuthFilter(@Value("${jwt.secret}") String secreto) {
        this.llaveSecreta = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
    }

    /** Intercepta cada request: si trae un JWT válido, autentica; si no, deja el contexto vacío y continúa la cadena. */
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
