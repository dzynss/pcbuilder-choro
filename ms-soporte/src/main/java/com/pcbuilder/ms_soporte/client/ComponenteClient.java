package com.pcbuilder.ms_soporte.client;

import com.pcbuilder.ms_soporte.dto.ComponenteResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Cliente Feign hacia ms-componentes (URL configurada en {@code ms.componentes.url},
 * env var {@code MS_COMPONENTES_URL}). Usado por SoporteService para verificar que el
 * componente reclamado en un ticket exista realmente antes de crearlo/actualizarlo.
 */
@FeignClient(name = "ms-componentes", url = "${ms.componentes.url}")
public interface ComponenteClient {

    /** Obtiene un componente por ID desde ms-componentes; lanza FeignException.NotFound si no existe. */
    @GetMapping("/api/componentes/{id}")
    ComponenteResponseDTO buscarPorId(@PathVariable Long id);
}
