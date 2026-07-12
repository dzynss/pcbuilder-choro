package com.pcbuilder.ms_resenas.client;

import com.pcbuilder.ms_resenas.dto.ComponenteResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Cliente Feign hacia ms-componentes (puerto 8085), URL base tomada de la property
 * {@code ms.componentes.url} (env var MS_COMPONENTES_URL, default localhost:8085).
 * Usado por {@code ResenaService} para validar/obtener el componente real antes de crear/actualizar una reseña.
 */
@FeignClient(name = "ms-componentes", url = "${ms.componentes.url}")
public interface ComponenteClient {

    /** Obtiene un componente por ID desde ms-componentes; responde 404 (FeignException.NotFound) si no existe. */
    @GetMapping("/api/componentes/{id}")
    ComponenteResponseDTO buscarPorId(@PathVariable Long id);
}
