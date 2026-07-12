package com.pcbuilder.ms_usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO de request para POST /api/usuarios/login; validado con Bean Validation
 * y consumido por {@link com.pcbuilder.ms_usuarios.service.UsuarioService#login}.
 */
public record LoginRequestDTO(
        @NotBlank @Email(message = "Correo con formato inválido") String correo,
        @NotBlank(message = "La contraseña no puede ir vacía") String password
) {}
