package com.pcbuilder.ms_notificaciones.dto;

/**
 * Copia local del contrato que expone ms-usuarios; se usa solo para deserializar la respuesta
 * de {@code UsuarioClient.buscarPorId} (Feign) al validar que el usuario destinatario exista.
 */
public record UsuarioResponseDTO(
        Long id,
        String nombre,
        String correo,
        String rol
) {}
