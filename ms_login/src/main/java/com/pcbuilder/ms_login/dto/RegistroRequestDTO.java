package com.pcbuilder.ms_login.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para POST /api/auth/register. Copia local del contrato de
 * ms-usuarios#UsuarioRequestDTO; se reenvía tal cual como cuerpo de la llamada Feign
 * a ms-usuarios en {@link com.pcbuilder.ms_login.client.UsuarioClient#registrar}.
 */
public record RegistroRequestDTO(

        @NotBlank(message = "El nombre no puede ir vacío")
        @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$", message = "El nombre solo puede tener letras")
        String nombre,

        @NotBlank(message = "El correo no puede ir vacío")
        @Email(message = "Ese correo no tiene un formato válido")
        String correo,

        @NotBlank(message = "La contraseña no puede ir vacía")
        @Size(min = 4, max = 100, message = "La contraseña debe tener entre 4 y 100 caracteres")
        String password,

        @NotBlank(message = "El rol no puede ir vacío")
        @Pattern(regexp = "ADMIN|USER", message = "El rol debe ser ADMIN o USER")
        String rol
) {}
