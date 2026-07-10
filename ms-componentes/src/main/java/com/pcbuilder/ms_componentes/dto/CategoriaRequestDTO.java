package com.pcbuilder.ms_componentes.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoriaRequestDTO(

        @NotBlank(message = "El nombre no puede ir vacío")
        String nombre
) {}
