package com.pcbuilder.ms_componentes.dto;

/** DTO que tambien consumen otros microservicios (ms_cotizaciones, ms-resenas, ms-soporte) via Feign. */
public record ComponenteResponseDTO(
        Long id,
        String nombre,
        String marca,
        Double precio,
        Integer stock,
        String categoria
) {}
