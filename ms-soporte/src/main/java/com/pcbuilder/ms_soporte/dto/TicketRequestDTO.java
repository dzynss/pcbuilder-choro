package com.pcbuilder.ms_soporte.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TicketRequestDTO(

        @NotNull(message = "Falta el ID del usuario que reclama")
        Long idUsuario,

        @NotNull(message = "Falta el ID de la pieza con problemas")
        Long idComponente,

        @NotBlank(message = "Ponle una descripción al problema")
        String descripcion
) {}
