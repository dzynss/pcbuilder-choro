package com.pcbuilder.ms_notificaciones.dto;

import java.time.LocalDateTime;

public record NotificacionResponseDTO(
        Long id,
        Long idUsuario,
        String tipoMensaje,
        String contenido,
        String estado,
        LocalDateTime fechaEnvio
) {}
