package com.pcbuilder.ms_login.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO de entrada para POST /api/auth/login (recibido en {@link com.pcbuilder.ms_login.controller.AuthController#login}).
 * Se valida con Bean Validation (fallas capturadas por GlobalExceptionHandler → 400) y se
 * reenvía tal cual como cuerpo de la llamada Feign a ms-usuarios en {@link com.pcbuilder.ms_login.client.UsuarioClient#login}.
 */
public record LoginRequestDTO(
        @NotBlank @Email(message = "Correo con formato inválido") String correo,
        @NotBlank(message = "La contraseña no puede ir vacía") String password
) {}
