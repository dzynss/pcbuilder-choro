package com.pcbuilder.presupuesto_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record PresupuestoRequestDTO(

        @NotNull(message = "El total aprobado es obligatorio")
        @Min(value = 0, message = "El total aprobado no puede ser menor a 0")
        Integer totalAprobado,

        @NotNull(message = "El total gastado es obligatorio")
        @Min(value = 0, message = "El total gastado no puede ser menor a 0")
        Integer totalGastado,

        @NotNull(message = "La fecha de registro es obligatoria")
        LocalDateTime fechaRegistro,

        @NotBlank(message = "El estado es obligatorio")
        String estado
) {}
