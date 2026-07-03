package com.example.presupuesto_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PresupuestoDTO {
    private Long id;
    private int totalAprobado;
    private int totalGastado;
    private Date fechaRegistro;
    private String estado;

}
