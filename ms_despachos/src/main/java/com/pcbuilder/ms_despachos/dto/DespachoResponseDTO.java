package com.pcbuilder.ms_despachos.dto;

import java.time.LocalDateTime;

public record DespachoResponseDTO(
        Long id,
        Long idUsuario,
        String direccionEnvio,
        String empresaTransporte,
        String estadoSeguimiento,
        LocalDateTime fechaDespacho
) {}
