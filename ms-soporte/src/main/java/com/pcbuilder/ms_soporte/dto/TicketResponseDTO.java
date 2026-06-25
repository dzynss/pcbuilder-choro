package com.pcbuilder.ms_soporte.dto;

import java.time.LocalDateTime;

public record TicketResponseDTO(
        Long id,
        Long idUsuario,
        Long idComponente,
        String descripcion,
        String estado,
        LocalDateTime fechaCreacion
) {}
