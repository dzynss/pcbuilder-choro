package com.pcbuilder.ms_despachos.client;

import com.pcbuilder.ms_despachos.dto.UsuarioResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Cliente Feign hacia ms-usuarios (puerto 8083). La URL base viene de la property
 * ms.usuarios.url (env var MS_USUARIOS_URL, default localhost:8083).
 * Usado por DespachoService para validar que el usuario destinatario del despacho exista.
 */
@FeignClient(name = "ms-usuarios", url = "${ms.usuarios.url}")
public interface UsuarioClient {

    /** Llama a GET /api/usuarios/{id} en ms-usuarios; lanza FeignException si no existe o si hay error de comunicación. */
    @GetMapping("/api/usuarios/{id}")
    UsuarioResponseDTO buscarPorId(@PathVariable Long id);
}
