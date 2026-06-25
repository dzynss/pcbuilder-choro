package com.pcbuilder.ms_cotizaciones.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CotizacionRequestDTO(

        @NotNull(message = "Debe indicar el ID del usuario")
        Long idUsuario,

        @NotNull(message = "Debe indicar el ID del componente")
        Long idComponente,

        @NotNull(message = "Debe indicar la cantidad")
        @Min(value = 1, message = "La cantidad mínima es 1")
        Integer cantidad
) {}
