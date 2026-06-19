package com.pcbuilder.ms_soporte.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "soporte_tickets")
@Data
public class TicketSoporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Falta el ID del loco que está reclamando")
    private Long idUsuario;

    @NotNull(message = "Falta el ID de la pieza mala")
    private Long idComponente;

    @NotBlank(message = "Póngale una descripción al atado po, no seai pajarón")
    private String descripcion;

    private String estado; // ABIERTO o CERRADO

    private LocalDateTime fechaCreacion;
}