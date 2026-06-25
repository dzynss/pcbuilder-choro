package com.pcbuilder.ms_despachos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DespachoRequestDTO(

        @NotNull(message = "Falta el ID del cliente al que le mandamos la encomienda")
        Long idUsuario,

        @NotBlank(message = "Ponle la dirección o la encomienda no llega")
        String direccionEnvio,

        String empresaTransporte
) {}
