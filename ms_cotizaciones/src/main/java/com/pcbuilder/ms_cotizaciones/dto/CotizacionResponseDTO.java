package com.pcbuilder.ms_cotizaciones.dto;

/**
 * DTO de salida de una cotización, devuelto por {@code CotizacionController}
 * (envuelto en {@code EntityModel} para agregar enlaces HATEOAS).
 * Mapea la entidad {@code Cotizacion} vía {@code CotizacionService.aResponseDTO}.
 */
public record CotizacionResponseDTO(
        Long id,
        Long idUsuario,
        Long idComponente,
        Integer cantidad,
        Double total
) {}
