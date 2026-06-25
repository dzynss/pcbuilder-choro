package com.pcbuilder.ms_cotizaciones.client;

import com.pcbuilder.ms_cotizaciones.dto.UsuarioResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-usuarios", url = "${ms.usuarios.url}")
public interface UsuarioClient {

    @GetMapping("/api/usuarios/{id}")
    UsuarioResponseDTO buscarPorId(@PathVariable Long id);
}
