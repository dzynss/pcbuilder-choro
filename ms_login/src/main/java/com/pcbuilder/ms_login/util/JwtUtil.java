package com.pcbuilder.ms_login.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    
    private static final Key LLAVE_SECRETA = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long TIEMPO_EXPIRACION = 3600000; // 1 horita (lo que duro YIAAAA)

    public String generarToken(String correo) {
        return Jwts.builder()
                .setSubject(correo)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + TIEMPO_EXPIRACION))
                .signWith(LLAVE_SECRETA)
                .compact();
    }
}