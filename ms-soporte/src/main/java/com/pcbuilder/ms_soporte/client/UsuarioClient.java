package com.pcbuilder.ms_soporte.client;

import com.pcbuilder.ms_soporte.dto.UsuarioResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Cliente Feign hacia ms-usuarios (URL configurada en {@code ms.usuarios.url},
 * env var {@code MS_USUARIOS_URL}). Usado por SoporteService para verificar que el
 * usuario que reclama en un ticket exista realmente antes de crearlo/actualizarlo.
 */
@FeignClient(name = "ms-usuarios", url = "${ms.usuarios.url}")
public interface UsuarioClient {

    /** Obtiene un usuario por ID desde ms-usuarios; lanza FeignException.NotFound si no existe. */
    @GetMapping("/api/usuarios/{id}")
    UsuarioResponseDTO buscarPorId(@PathVariable Long id);
}
