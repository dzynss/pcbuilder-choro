package com.pcbuilder.ms_componentes.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO de entrada para crear/actualizar una categoría, usado por
 * {@link com.pcbuilder.ms_componentes.controller.CategoriaController} (POST/PUT).
 * La validación fallida es capturada por GlobalExceptionHandler (400 Bad Request).
 */
public record CategoriaRequestDTO(

        @NotBlank(message = "El nombre no puede ir vacío")
        String nombre
) {}
