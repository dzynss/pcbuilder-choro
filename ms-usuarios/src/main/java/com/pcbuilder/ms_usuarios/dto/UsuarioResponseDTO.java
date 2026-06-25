package com.pcbuilder.ms_usuarios.dto;

/** Nunca incluye el password: es lo que se expone hacia afuera (Gateway, Swagger, otros MS). */
public record UsuarioResponseDTO(
        Long id,
        String nombre,
        String correo,
        String rol
) {}
