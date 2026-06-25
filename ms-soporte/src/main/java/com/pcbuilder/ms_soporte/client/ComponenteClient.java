package com.pcbuilder.ms_soporte.client;

import com.pcbuilder.ms_soporte.dto.ComponenteResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-componentes", url = "${ms.componentes.url}")
public interface ComponenteClient {

    @GetMapping("/api/componentes/{id}")
    ComponenteResponseDTO buscarPorId(@PathVariable Long id);
}
