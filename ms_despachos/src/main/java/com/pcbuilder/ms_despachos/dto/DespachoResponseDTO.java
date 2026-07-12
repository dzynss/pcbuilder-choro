package com.pcbuilder.ms_despachos.dto;

import java.time.LocalDateTime;

/**
 * DTO de salida de un despacho, devuelto por DespachoController (envuelto en HATEOAS EntityModel)
 * y construido por DespachoService.aResponseDTO a partir de la entidad Despacho.
 */
public record DespachoResponseDTO(
        Long id,
        Long idUsuario,
        String direccionEnvio,
        String empresaTransporte,
        String estadoSeguimiento,
        LocalDateTime fechaDespacho
) {}
