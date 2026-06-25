package com.pcbuilder.ms_componentes.dto;

import jakarta.validation.constraints.*;

public record ComponenteRequestDTO(

        @NotBlank(message = "El nombre no puede ir vacío")
        String nombre,

        @NotBlank(message = "La marca no puede ir vacía")
        String marca,

        @NotNull(message = "El precio no puede ir vacío")
        @Positive(message = "El precio debe ser mayor a 0")
        Double precio,

        @NotNull(message = "El stock no puede ir vacío")
        @Min(value = 0, message = "El stock no puede ser negativo")
        Integer stock,

        @NotNull(message = "Debe indicar la categoría")
        Long idCategoria
) {}
