package com.pcbuilder.ms_login.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * Utilidad para emitir JWT (HS256) tras un login exitoso.
 * La clave secreta se deriva de la property jwt.secret, por lo que es estable entre reinicios
 * y puede compartirse entre instancias. IMPORTANTE: ningún otro microservicio valida
 * actualmente este JWT (no hay filtro de seguridad en las demás rutas /api/**), así que
 * el token es solo informativo hasta que se implemente esa validación.
 */
@Component
public class JwtUtil {

    private static final long TIEMPO_EXPIRACION = 3600000; // 1 horita (lo que duro YIAAAA)

    private final Key llaveSecreta;

    /** Construye la clave HMAC a partir de la property jwt.secret. */
    public JwtUtil(@Value("${jwt.secret}") String secreto) {
        this.llaveSecreta = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Genera un JWT HS256 con el correo como subject y expiración de 1 hora.
     * Invocado por {@link com.pcbuilder.ms_login.service.AuthService#login} tras validar
     * las credenciales contra ms-usuarios.
     */
    public String generarToken(String correo) {
        return Jwts.builder()
                .setSubject(correo)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + TIEMPO_EXPIRACION))
                .signWith(llaveSecreta)
                .compact();
    }
}
