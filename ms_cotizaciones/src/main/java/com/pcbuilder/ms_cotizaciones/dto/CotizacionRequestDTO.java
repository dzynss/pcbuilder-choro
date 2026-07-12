package com.pcbuilder.ms_cotizaciones.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de entrada para crear/actualizar una cotización, usado por {@code CotizacionController}.
 * Nótese que NO incluye precio ni total: esos los calcula {@code CotizacionService}
 * con el precio real obtenido de ms-componentes vía {@code ComponenteClient}.
 */
public record CotizacionRequestDTO(

        @NotNull(message = "Debe indicar el ID del usuario")
        Long idUsuario,

        @NotNull(message = "Debe indicar el ID del componente")
        Long idComponente,

        @NotNull(message = "Debe indicar la cantidad")
        @Min(value = 1, message = "La cantidad mínima es 1")
        Integer cantidad
) {}
