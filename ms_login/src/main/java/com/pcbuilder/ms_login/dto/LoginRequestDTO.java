package com.pcbuilder.ms_login.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank @Email(message = "Correo con formato inválido") String correo,
        @NotBlank(message = "La contraseña no puede ir vacía") String password
) {}
