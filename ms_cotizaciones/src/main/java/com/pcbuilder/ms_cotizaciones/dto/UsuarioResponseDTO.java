package com.pcbuilder.ms_cotizaciones.dto;

/** Copia local del contrato que expone ms-usuarios; se usa solo para deserializar la respuesta del Feign Client. */
public record UsuarioResponseDTO(
        Long id,
        String nombre,
        String correo,
        String rol
) {}
