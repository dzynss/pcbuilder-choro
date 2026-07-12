package com.pcbuilder.ms_inventario.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de entrada para crear/actualizar un registro de inventario.
 * Recibido por InventarioController (guardar/actualizar), validado con Bean Validation antes de llegar a InventarioService.
 */
public record InventarioRequestDTO(

        @NotNull(message = "El ID del componente no puede ir vacío")
        Long idComponente,

        @NotNull(message = "Tenís que poner una cantidad")
        @Min(value = 0, message = "El stock no puede ser negativo")
        Integer cantidadDisponible,

        String ubicacionBodega
) {}
