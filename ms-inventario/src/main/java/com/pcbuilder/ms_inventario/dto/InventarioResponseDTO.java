package com.pcbuilder.ms_inventario.dto;

import java.time.LocalDateTime;

/**
 * DTO de salida que expone el estado de un registro de inventario sin exponer la entity directamente.
 * Es armado por InventarioService y devuelto (envuelto en EntityModel/HATEOAS) por InventarioController.
 */
public record InventarioResponseDTO(
        Long id,
        Long idComponente,
        Integer cantidadDisponible,
        String ubicacionBodega,
        LocalDateTime ultimaActualizacion
) {}
