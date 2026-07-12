package com.pcbuilder.ms_componentes.dto;

/**
 * DTO de salida de un componente, devuelto por
 * {@link com.pcbuilder.ms_componentes.controller.ComponenteController} y construido en
 * {@link com.pcbuilder.ms_componentes.service.ComponenteService}. Nunca se expone la
 * entity {@link com.pcbuilder.ms_componentes.entity.Componente} directamente.
 * También es el contrato que consumen otros microservicios vía Feign:
 * ms_cotizaciones lo usa para obtener el precio real al calcular el total de una
 * cotización, ms-resenas para validar el componente reseñado y ms-soporte para
 * validar el componente asociado a un ticket.
 */
public record ComponenteResponseDTO(
        Long id,
        String nombre,
        String marca,
        Double precio,
        Integer stock,
        String categoria
) {}
