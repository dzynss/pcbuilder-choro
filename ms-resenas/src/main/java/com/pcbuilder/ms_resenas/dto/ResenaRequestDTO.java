package com.pcbuilder.ms_resenas.dto;

import jakarta.validation.constraints.*;

public record ResenaRequestDTO(

        @NotBlank(message = "El autor no puede ir vacío")
        String autor,

        @NotBlank(message = "El comentario no puede ir vacío")
        String comentario,

        @NotNull(message = "La calificación no puede ir vacía")
        @Min(value = 1, message = "La calificación mínima es 1 estrella")
        @Max(value = 5, message = "La calificación máxima es 5 estrellas")
        Integer calificacion,

        @NotNull(message = "Debe indicar a qué componente pertenece la reseña")
        Long idComponente
) {}
