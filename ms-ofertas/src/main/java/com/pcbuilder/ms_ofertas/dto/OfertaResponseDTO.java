package com.pcbuilder.ms_ofertas.dto;

/**
 * DTO de salida que expone los datos públicos de un cupón sin exponer la entity directamente.
 * Es armado por OfertaService y devuelto (envuelto en EntityModel/HATEOAS) por OfertaController.
 */
public record OfertaResponseDTO(
        Long id,
        String codigo,
        Integer porcentajeDescuento,
        boolean activa
) {}
