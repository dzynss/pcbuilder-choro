package com.pcbuilder.ms_resenas.dto;

/** Copia local del contrato que expone ms-componentes; se usa solo para deserializar la respuesta del Feign Client. */
public record ComponenteResponseDTO(
        Long id,
        String nombre,
        String marca,
        Double precio,
        Integer stock,
        String categoria
) {}
