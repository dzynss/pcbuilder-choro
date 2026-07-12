package com.pcbuilder.ms_resenas.dto;

/**
 * DTO de salida de una reseña, usado por {@code ResenaController}/{@code ResenaService} para no exponer
 * la entidad {@code Resena} directamente por HTTP; suele envolverse en {@code EntityModel} para agregar links HATEOAS.
 */
public record ResenaResponseDTO(
        Long id,
        String autor,
        String comentario,
        Integer calificacion,
        Long idComponente
) {}
