package com.pcbuilder.ms_cotizaciones.dto;

public record CotizacionResponseDTO(
        Long id,
        Long idUsuario,
        Long idComponente,
        Integer cantidad,
        Double total
) {}
