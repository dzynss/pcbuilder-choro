package com.pcbuilder.ms_despachos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de entrada para crear/actualizar un despacho (usado por DespachoController en POST y PUT /api/despachos).
 * Las validaciones de Bean Validation se disparan por @Valid y son capturadas por
 * GlobalExceptionHandler.handleValidacion (400 Bad Request).
 */
public record DespachoRequestDTO(

        @NotNull(message = "Falta el ID del cliente al que le mandamos la encomienda")
        Long idUsuario,

        @NotBlank(message = "Ponle la dirección o la encomienda no llega")
        String direccionEnvio,

        String empresaTransporte
) {}
