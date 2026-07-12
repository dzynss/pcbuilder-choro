package com.pcbuilder.ms_usuarios.security;

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
 * Filtro de Servlet (ejecutado una vez por request) que valida el JWT emitido por
 * ms_login usando la misma clave secreta ({@code jwt.secret}). Si el token es válido,
 * puebla el {@link SecurityContextHolder} para que {@link SecurityConfig} autorice la
 * petición; si falta o es inválido, limpia el contexto y deja que SecurityConfig
 * rechace la request con 401. Se registra como el filtro antes de
 * UsernamePasswordAuthenticationFilter en la cadena de Spring Security.
 */
@Component
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final Key llaveSecreta;

    /** Deriva la clave HMAC de firma a partir de la propiedad {@code jwt.secret} (compartida con ms_login y el gateway). */
    public JwtAuthFilter(@Value("${jwt.secret}") String secreto) {
        this.llaveSecreta = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
    }

    /** Extrae el header Authorization Bearer, valida el JWT y, si es válido, autentica al usuario (correo como principal) para esta request. */
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
