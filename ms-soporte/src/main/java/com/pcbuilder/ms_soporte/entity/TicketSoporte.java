package com.pcbuilder.ms_soporte.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Entidad JPA que representa un ticket de soporte. Se mapea a la tabla
 * {@code soporte_tickets}, cuyo esquema real vive en Liquibase
 * (db.changelog-master.xml); ddl-auto es "validate", así que esta clase debe
 * coincidir exactamente con esa tabla. Lombok {@code @Data} genera getters/setters.
 */
@Entity
@Table(name = "soporte_tickets")
@Data
public class TicketSoporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** ID del usuario que reclama; se valida contra ms-usuarios vía Feign antes de guardar. */
    @NotNull(message = "Falta el ID del loco que está reclamando")
    private Long idUsuario;

    /** ID del componente reclamado; se valida contra ms-componentes vía Feign antes de guardar. */
    @NotNull(message = "Falta el ID de la pieza mala")
    private Long idComponente;

    @NotBlank(message = "Póngale una descripción al atado po, no seai pajarón")
    private String descripcion;

    private String estado; // ABIERTO o CERRADO

    private LocalDateTime fechaCreacion;
}