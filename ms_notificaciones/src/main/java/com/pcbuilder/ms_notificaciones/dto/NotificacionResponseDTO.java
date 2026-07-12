package com.pcbuilder.ms_notificaciones.dto;

import java.time.LocalDateTime;

/**
 * DTO de salida que representa una notificación persistida; devuelto por {@code NotificacionService}
 * y expuesto por {@code NotificacionController} (envuelto en {@code EntityModel} para HATEOAS).
 */
public record NotificacionResponseDTO(
        Long id,
        Long idUsuario,
        String tipoMensaje,
        String contenido,
        String estado,
        LocalDateTime fechaEnvio
) {}
