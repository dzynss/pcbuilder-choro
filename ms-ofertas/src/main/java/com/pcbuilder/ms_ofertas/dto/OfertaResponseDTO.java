package com.pcbuilder.ms_ofertas.dto;

public record OfertaResponseDTO(
        Long id,
        String codigo,
        Integer porcentajeDescuento,
        boolean activa
) {}
