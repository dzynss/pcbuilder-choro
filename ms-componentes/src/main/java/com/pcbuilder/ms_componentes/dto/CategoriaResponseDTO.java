package com.pcbuilder.ms_componentes.dto;

/**
 * DTO de salida de una categoría, devuelto por
 * {@link com.pcbuilder.ms_componentes.controller.CategoriaController} y construido en
 * {@link com.pcbuilder.ms_componentes.service.CategoriaService} a partir de la entity
 * {@link com.pcbuilder.ms_componentes.entity.Categoria}.
 */
public record CategoriaResponseDTO(
        Long id,
        String nombre
) {}
