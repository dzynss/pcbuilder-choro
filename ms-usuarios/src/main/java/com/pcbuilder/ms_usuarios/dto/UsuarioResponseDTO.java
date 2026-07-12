package com.pcbuilder.ms_usuarios.dto;

/**
 * DTO de respuesta de {@link com.pcbuilder.ms_usuarios.controller.UsuarioController} y
 * {@link com.pcbuilder.ms_usuarios.service.UsuarioService}; nunca incluye el password.
 * Es lo que se expone hacia afuera (gateway, Swagger, y otros microservicios que
 * consultan usuarios vía Feign: ms_login, ms_cotizaciones, ms-soporte, ms_despachos, ms_notificaciones).
 */
public record UsuarioResponseDTO(
        Long id,
        String nombre,
        String correo,
        String rol
) {}
