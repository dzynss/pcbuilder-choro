package com.pcbuilder.ms_soporte.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para crear/actualizar un ticket (SoporteController). idUsuario e
 * idComponente se validan vía Feign contra ms-usuarios y ms-componentes en SoporteService
 * antes de persistir, en lugar de confiar directamente en el request.
 */
public record TicketRequestDTO(

        @NotNull(message = "Falta el ID del usuario que reclama")
        Long idUsuario,

        @NotNull(message = "Falta el ID de la pieza con problemas")
        Long idComponente,

        @NotBlank(message = "Ponle una descripción al problema")
        @Size(max = 255, message = "La descripción no puede tener más de 255 caracteres")
        String descripcion
) {}
