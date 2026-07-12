package com.pcbuilder.ms_cotizaciones.client;

import com.pcbuilder.ms_cotizaciones.dto.ComponenteResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Cliente Feign hacia ms-componentes (puerto 8085, URL configurable vía
 * {@code ms.componentes.url} / env {@code MS_COMPONENTES_URL}).
 * Usado por {@code CotizacionService} para validar que el componente exista y,
 * sobre todo, para obtener su precio REAL (nunca se confía en el precio del request).
 */
@FeignClient(name = "ms-componentes", url = "${ms.componentes.url}")
public interface ComponenteClient {

    /** Obtiene un componente por ID desde ms-componentes; lanza FeignException.NotFound si no existe (capturado en el service). */
    @GetMapping("/api/componentes/{id}")
    ComponenteResponseDTO buscarPorId(@PathVariable Long id);
}
