package com.pcbuilder.ms_login.client;

import com.pcbuilder.ms_login.dto.LoginRequestDTO;
import com.pcbuilder.ms_login.dto.UsuarioResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Cliente Feign hacia ms-usuarios (puerto 8083). URL base tomada de la property
 * ms.usuarios.url (env var MS_USUARIOS_URL, default localhost:8083).
 * Es invocado por {@link com.pcbuilder.ms_login.service.AuthService#login} para validar
 * las credenciales reales del usuario; las fallas de red/HTTP se capturan allí como
 * FeignException y se traducen a CredencialesInvalidasException o ErrorComunicacionException.
 */
@FeignClient(name = "ms-usuarios", url = "${ms.usuarios.url}")
public interface UsuarioClient {

    /** Llama a POST /api/usuarios/login en ms-usuarios para verificar correo/contraseña. */
    @PostMapping("/api/usuarios/login")
    UsuarioResponseDTO login(@RequestBody LoginRequestDTO credenciales);
}
