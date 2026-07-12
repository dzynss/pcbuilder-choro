package com.pcbuilder.ms_login.dto;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para GET /api/auth/historial; representa un registro de {@link com.pcbuilder.ms_login.entity.HistorialLogin}.
 * Construido en {@link com.pcbuilder.ms_login.service.AuthService#listarHistorial} y devuelto por {@link com.pcbuilder.ms_login.controller.AuthController#historial}.
 */
public record HistorialResponseDTO(
        Long id,
        String correoUsuario,
        LocalDateTime fechaHora,
        String estado
) {}
