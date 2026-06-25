package com.pcbuilder.ms_inventario.dto;

import java.time.LocalDateTime;

public record InventarioResponseDTO(
        Long id,
        Long idComponente,
        Integer cantidadDisponible,
        String ubicacionBodega,
        LocalDateTime ultimaActualizacion
) {}
