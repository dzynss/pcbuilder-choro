package com.pcbuilder.ms_usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UsuarioRequestDTO(

        @NotBlank(message = "Vo' soy vio, el nombre no puede ir vacío")
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
