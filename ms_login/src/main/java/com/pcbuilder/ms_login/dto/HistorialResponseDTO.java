package com.pcbuilder.ms_login.dto;

import java.time.LocalDateTime;

public record HistorialResponseDTO(
        Long id,
        String correoUsuario,
        LocalDateTime fechaHora,
        String estado
) {}
