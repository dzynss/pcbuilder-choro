package com.pcbuilder.ms_cotizaciones.client;

import com.pcbuilder.ms_cotizaciones.dto.UsuarioResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Cliente Feign hacia ms-usuarios (puerto 8083, URL configurable vía
 * {@code ms.usuarios.url} / env {@code MS_USUARIOS_URL}).
 * Usado por {@code CotizacionService} para verificar que el usuario de la cotización exista.
 */
@FeignClient(name = "ms-usuarios", url = "${ms.usuarios.url}")
public interface UsuarioClient {

    /** Obtiene un usuario por ID desde ms-usuarios; lanza FeignException.NotFound si no existe (capturado en el service). */
    @GetMapping("/api/usuarios/{id}")
    UsuarioResponseDTO buscarPorId(@PathVariable Long id);
}
