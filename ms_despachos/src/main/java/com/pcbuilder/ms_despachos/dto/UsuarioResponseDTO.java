package com.pcbuilder.ms_despachos.dto;

/** Copia local del contrato que expone ms-usuarios; se usa solo para deserializar la respuesta de UsuarioClient (Feign). */
public record UsuarioResponseDTO(
        Long id,
        String nombre,
        String correo,
        String rol
) {}
