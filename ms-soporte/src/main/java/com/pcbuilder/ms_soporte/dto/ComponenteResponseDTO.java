package com.pcbuilder.ms_soporte.dto;

/**
 * Copia local del contrato que expone ms-componentes; se usa solo para deserializar
 * la respuesta de {@link com.pcbuilder.ms_soporte.client.ComponenteClient}.
 */
public record ComponenteResponseDTO(
        Long id,
        String nombre,
        String marca,
        Double precio,
        Integer stock,
        String categoria
) {}
