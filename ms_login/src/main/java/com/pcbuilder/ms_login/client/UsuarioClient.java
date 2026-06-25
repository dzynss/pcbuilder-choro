package com.pcbuilder.ms_login.client;

import com.pcbuilder.ms_login.dto.LoginRequestDTO;
import com.pcbuilder.ms_login.dto.UsuarioResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-usuarios", url = "${ms.usuarios.url}")
public interface UsuarioClient {

    @PostMapping("/api/usuarios/login")
    UsuarioResponseDTO login(@RequestBody LoginRequestDTO credenciales);
}
