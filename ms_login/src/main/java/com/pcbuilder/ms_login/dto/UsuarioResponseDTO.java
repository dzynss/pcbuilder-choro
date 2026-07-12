package com.pcbuilder.ms_login.dto;

/**
 * Copia local del contrato de respuesta que expone ms-usuarios en POST /api/usuarios/login;
 * se usa solo para deserializar la respuesta de {@link com.pcbuilder.ms_login.client.UsuarioClient#login}.
 */
public record UsuarioResponseDTO(
        Long id,
        String nombre,
        String correo,
        String rol
) {}
