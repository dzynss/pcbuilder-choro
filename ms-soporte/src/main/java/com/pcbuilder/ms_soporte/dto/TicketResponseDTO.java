package com.pcbuilder.ms_soporte.dto;

import java.time.LocalDateTime;

/**
 * DTO de salida de un ticket de soporte, devuelto por SoporteController/SoporteService
 * (envuelto en {@code EntityModel} con enlaces HATEOAS en el controller).
 */
public record TicketResponseDTO(
        Long id,
        Long idUsuario,
        Long idComponente,
        String descripcion,
        String estado,
        LocalDateTime fechaCreacion
) {}
