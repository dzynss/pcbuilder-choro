package com.pcbuilder.presupuesto_service.dto;

import java.time.LocalDateTime;

public record PresupuestoResponseDTO(
        Long id,
        Integer totalAprobado,
        Integer totalGastado,
        LocalDateTime fechaRegistro,
        String estado
) {}
