package com.pcbuilder.ms_notificaciones.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record NotificacionRequestDTO(

        @NotNull(message = "Falta el ID del usuario destinatario")
        Long idUsuario,

        @NotBlank(message = "Ponle si es EMAIL o SMS")
        @Pattern(regexp = "EMAIL|SMS", message = "El tipo de mensaje debe ser EMAIL o SMS")
        String tipoMensaje,

        @NotBlank(message = "El mensaje no puede ir vacío")
        String contenido
) {}
