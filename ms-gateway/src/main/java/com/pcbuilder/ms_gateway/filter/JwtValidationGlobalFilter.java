package com.pcbuilder.ms_gateway.filter;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.Key;

/**
 * Filtro global del gateway que valida el JWT emitido por ms_login antes de
 * enrutar la peticion al microservicio de negocio correspondiente. Es una
 * primera linea de defensa a nivel de gateway; cada microservicio de negocio
 * valida el token de forma independiente en su propio filtro.
 */
@Component
@Slf4j
public class JwtValidationGlobalFilter implements GlobalFilter, Ordered {

    private static final String PREFIJO_BEARER = "Bearer ";
    private static final String PATH_AUTH = "/api/auth";

    private final Key llaveSecreta;

    public JwtValidationGlobalFilter(@Value("${jwt.secret}") String secreto) {
        this.llaveSecreta = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        boolean esRegistroDeUsuario = path.equals("/api/usuarios") && request.getMethod() == HttpMethod.POST;

        if (path.startsWith(PATH_AUTH) || esRegistroDeUsuario) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith(PREFIJO_BEARER)) {
            log.warn("Peticion rechazada por falta de token JWT en path: {}", path);
            return rechazar(exchange);
        }

        String token = authHeader.substring(PREFIJO_BEARER.length());

        try {
            Jwts.parserBuilder()
                    .setSigningKey(llaveSecreta)
                    .build()
                    .parseClaimsJws(token);
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Peticion rechazada por token JWT invalido o expirado en path: {}", path);
            return rechazar(exchange);
        }

        return chain.filter(exchange);
    }

    private Mono<Void> rechazar(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        String body = String.format(
                "{\"timestamp\":\"%s\",\"status\":401,\"mensaje\":\"Token JWT ausente o inválido\"}",
                java.time.Instant.now());

        DataBufferFactory bufferFactory = response.bufferFactory();
        DataBuffer buffer = bufferFactory.wrap(body.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }
}
