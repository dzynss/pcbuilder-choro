package com.pcbuilder.ms_resenas.dto;

public record ResenaResponseDTO(
        Long id,
        String autor,
        String comentario,
        Integer calificacion,
        Long idComponente
) {}
