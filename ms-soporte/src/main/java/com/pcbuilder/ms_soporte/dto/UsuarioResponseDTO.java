package com.pcbuilder.ms_soporte.dto;

/**
 * Copia local del contrato que expone ms-usuarios; se usa solo para deserializar
 * la respuesta de {@link com.pcbuilder.ms_soporte.client.UsuarioClient}.
 */
public record UsuarioResponseDTO(
        Long id,
        String nombre,
        String correo,
        String rol
) {}
