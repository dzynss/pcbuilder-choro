package com.pcbuilder.ms_ofertas.dto;

import jakarta.validation.constraints.*;

public record OfertaRequestDTO(

        @NotBlank(message = "El código del cupón no puede ir en blanco")
        @Size(max = 50, message = "El código del cupón no puede tener más de 50 caracteres")
        String codigo,

        @NotNull(message = "Póngale el porcentaje de descuento")
        @Min(value = 1, message = "Mínimo 1% de descuento")
        @Max(value = 100, message = "Máximo 100% de descuento")
        Integer porcentajeDescuento
) {}
