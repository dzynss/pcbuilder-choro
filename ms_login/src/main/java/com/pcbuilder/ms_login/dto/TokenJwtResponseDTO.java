package com.pcbuilder.ms_login.dto;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para GET /api/auth/tokens; representa un registro de {@link com.pcbuilder.ms_login.entity.TokenJwt}.
 * Construido en {@link com.pcbuilder.ms_login.service.AuthService#listarTokens} y devuelto por {@link com.pcbuilder.ms_login.controller.AuthController#tokens}.
 */
public record TokenJwtResponseDTO(
        Long id,
        String correoUsuario,
        String token,
        LocalDateTime fechaEmision,
        LocalDateTime fechaExpiracion
) {}
